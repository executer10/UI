package com.example.demo.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {
    // API 설정 상수
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int BUFFER_SIZE = 16384; // 버퍼 크기 증가
    private static final int STREAM_BATCH_SIZE = 512; // 응답 배치 크기 증가
    private static final char BACKSLASH = '\\';

    // 병렬 처리를 위한 스레드 풀
    private final ExecutorService executorService = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2));

    // 스트리밍 응답 생성 메인 로직 (비동기 버전)
    public CompletableFuture<Void> generateResponseStreamAsync(String message, String model, String sessionId,
            Consumer<String> onResponse) {
        return CompletableFuture.runAsync(() -> {
            generateResponseStream(message, model, sessionId, onResponse);
        }, executorService);
    }

    // 스트리밍 응답 생성 메인 로직
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        HttpURLConnection conn = null;
        try {
            // 요청 바디 구성 (직접 JSON 생성으로 변경)
            Map<String, Object> requestBody = createRequestBody(message, model, sessionId);
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            // HTTP 연결 초기화 및 최적화
            conn = createOptimizedConnection(jsonRequest);

            // 응답 코드 확인
            int statusCode = conn.getResponseCode();
            if (statusCode >= 400) {
                throw new RuntimeException("API 요청 실패: HTTP " + statusCode);
            }

            // 스트리밍 응답 처리
            processStreamResponse(conn, onResponse);

        } catch (IOException e) {
            throw new RuntimeException("스트리밍 응답 생성 실패: " + e.getMessage(), e);
        } finally {
            // 연결 리소스 해제
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // 요청 바디 생성 메서드 분리
    private Map<String, Object> createRequestBody(String message, String model, String sessionId) {
        Map<String, Object> requestBody = new HashMap<>(5, 1.0f); // 로드 팩터 최적화
        requestBody.put("model", model);
        requestBody.put("prompt", message);
        requestBody.put("stream", true);
        // 성능 최적화 옵션
        requestBody.put("raw", true);
        requestBody.put("num_predict", 4096);

        // 세션 ID 추가
        if (sessionId != null && !sessionId.isEmpty()) {
            Map<String, Object> options = new HashMap<>(2, 1.0f);
            options.put("num_ctx", 4096);
            options.put("session_id", sessionId);
            requestBody.put("options", options);
        }

        return requestBody;
    }

    // 최적화된 HTTP 연결 생성
    private HttpURLConnection createOptimizedConnection(String jsonRequest) throws IOException {
        URL url = new URL(OLLAMA_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        // 연결 및 읽기 타임아웃 설정
        conn.setConnectTimeout(10000); // 10초로 감소
        conn.setReadTimeout(120000); // 2분으로 증가 (긴 응답 고려)

        // 요청 데이터 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }

        return conn;
    }

    // 스트림 응답 처리 메서드 분리
    private void processStreamResponse(HttpURLConnection conn, Consumer<String> onResponse) throws IOException {
        try (InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8),
                        BUFFER_SIZE)) {

            // 응답 배치 처리를 위한 버퍼
            StringBuilder responseBuffer = new StringBuilder(STREAM_BATCH_SIZE * 2);
            StringBuilder batchBuffer = new StringBuilder(STREAM_BATCH_SIZE * 2);
            char[] buffer = new char[BUFFER_SIZE / 4];
            int charsRead;

            // 더 효율적인 문자 단위 읽기
            while ((charsRead = br.read(buffer)) != -1) {
                responseBuffer.append(buffer, 0, charsRead);

                // 완전한 라인이 있는지 처리
                int newlineIndex;
                int lastProcessedIndex = 0;

                while ((newlineIndex = responseBuffer.indexOf("\n", lastProcessedIndex)) != -1) {
                    String line = responseBuffer.substring(lastProcessedIndex, newlineIndex);
                    lastProcessedIndex = newlineIndex + 1;

                    if (!line.isEmpty()) {
                        processJsonResponseLine(line, batchBuffer, onResponse);
                    }
                }

                // 처리된 부분 제거
                if (lastProcessedIndex > 0) {
                    responseBuffer.delete(0, lastProcessedIndex);
                }
            }

            // 마지막 부분 처리
            if (responseBuffer.length() > 0) {
                String[] remainingLines = responseBuffer.toString().split("\n");
                for (String line : remainingLines) {
                    if (!line.isEmpty()) {
                        processJsonResponseLine(line, batchBuffer, onResponse);
                    }
                }
            }
        }
    }

    // JSON 응답 라인 처리
    private void processJsonResponseLine(String responseLine, StringBuilder batchBuffer, Consumer<String> onResponse) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseLine);
            boolean isDone = jsonResponse.has("done") && jsonResponse.get("done").asBoolean();

            if (jsonResponse.has("response")) {
                String response = jsonResponse.get("response").asText();
                if (response != null && !response.isEmpty()) {
                    batchBuffer.append(response);

                    // 일정 크기 이상이거나 완료 상태일 때 콜백 처리
                    if (batchBuffer.length() >= STREAM_BATCH_SIZE || isDone) {
                        String optimized = optimizeResponseString(batchBuffer.toString());
                        onResponse.accept(optimized);
                        batchBuffer.setLength(0); // 버퍼 초기화
                    }
                }
            } else if (isDone && batchBuffer.length() > 0) {
                // 'response' 필드가 없지만 done이고 버퍼에 내용이 있는 경우
                String optimized = optimizeResponseString(batchBuffer.toString());
                onResponse.accept(optimized);
                batchBuffer.setLength(0);
            }
        } catch (IOException e) {
            throw new RuntimeException("JSON 응답 파싱 오류: " + e.getMessage(), e);
        }
    }

    // 문자열 최적화 (더 빠른 버전)
    private String optimizeResponseString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 최적화: 치환이 필요한지 빠르게 확인
        if (input.indexOf(BACKSLASH) == -1) {
            return input;
        }

        // 문자별 처리로 단일 패스 최적화
        StringBuilder sb = new StringBuilder(input.length());
        boolean escape = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escape) {
                switch (c) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    default:
                        sb.append('\\').append(c);
                        break;
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                sb.append(c);
            }
        }

        // 마지막 이스케이프 문자 처리
        if (escape) {
            sb.append('\\');
        }

        return sb.toString();
    }

    // 동기식 응답 생성 메서드
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder(4096); // 더 큰 초기 크기로 최적화
        generateResponseStream(message, model, sessionId, fullResponse::append);
        return fullResponse.toString();
    }

    // 비동기식 응답 생성 메서드 (전체 응답)
    public CompletableFuture<String> generateResponseAsync(String message, String model, String sessionId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder fullResponse = new StringBuilder(4096);

        generateResponseStreamAsync(message, model, sessionId, response -> {
            synchronized (fullResponse) {
                fullResponse.append(response);
            }
        }).thenRun(() -> {
            synchronized (fullResponse) {
                future.complete(fullResponse.toString());
            }
        }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });

        return future;
    }

    // 서비스 리소스 해제 메서드 (스프링 자원 해제 이벤트에 연결될 수 있음)
    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // Spring의 @PreDestroy와 함께 사용
    public void destroy() {
        shutdown();
    }
}