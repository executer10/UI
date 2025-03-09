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
    private static final int BUFFER_SIZE = 16384;
    private static final int STREAM_BATCH_SIZE = 512;

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
            Map<String, Object> requestBody = createRequestBody(message, model, sessionId);
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            conn = createOptimizedConnection(jsonRequest);

            int statusCode = conn.getResponseCode();
            if (statusCode >= 400) {
                throw new RuntimeException("API 요청 실패: HTTP " + statusCode);
            }

            processStreamResponse(conn, onResponse);

        } catch (IOException e) {
            throw new RuntimeException("스트리밍 응답 생성 실패: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // 요청 바디 생성 메서드
    private Map<String, Object> createRequestBody(String message, String model, String sessionId) {
        Map<String, Object> requestBody = new HashMap<>(); // 기본 초기화로 충분함
        requestBody.put("model", model);
        requestBody.put("prompt", message);
        requestBody.put("stream", true);
        requestBody.put("raw", true);
        requestBody.put("num_predict", 4096);

        if (sessionId != null && !sessionId.isEmpty()) {
            Map<String, Object> options = new HashMap<>();
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

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(120000);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }

        return conn;
    }

    // 스트림 응답 처리 메서드
    private void processStreamResponse(HttpURLConnection conn, Consumer<String> onResponse) throws IOException {
        try (InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8),
                        BUFFER_SIZE)) {

            StringBuilder responseBuffer = new StringBuilder(STREAM_BATCH_SIZE * 2);
            StringBuilder batchBuffer = new StringBuilder(STREAM_BATCH_SIZE * 2);
            char[] buffer = new char[BUFFER_SIZE / 4];
            int charsRead;

            while ((charsRead = br.read(buffer)) != -1) {
                responseBuffer.append(buffer, 0, charsRead);

                int newlineIndex;
                int lastProcessedIndex = 0;

                while ((newlineIndex = responseBuffer.indexOf("\n", lastProcessedIndex)) != -1) {
                    String line = responseBuffer.substring(lastProcessedIndex, newlineIndex);
                    lastProcessedIndex = newlineIndex + 1;

                    if (!line.isEmpty()) {
                        processJsonResponseLine(line, batchBuffer, onResponse);
                    }
                }

                if (lastProcessedIndex > 0) {
                    responseBuffer.delete(0, lastProcessedIndex);
                }
            }

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

                    if (batchBuffer.length() >= STREAM_BATCH_SIZE || isDone) {
                        String optimized = optimizeResponseString(batchBuffer.toString());
                        onResponse.accept(optimized);
                        batchBuffer.setLength(0);
                    }
                }
            } else if (isDone && batchBuffer.length() > 0) {
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

        if (input.indexOf('\\') == -1) {
            return input;
        }

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

        if (escape) {
            sb.append('\\');
        }

        return sb.toString();
    }

    // 동기식 응답 생성 메서드
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder();
        generateResponseStream(message, model, sessionId, fullResponse::append);
        return fullResponse.toString();
    }

    // 비동기식 응답 생성 메서드 (전체 응답)
    public CompletableFuture<String> generateResponseAsync(String message, String model, String sessionId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder fullResponse = new StringBuilder();

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

    // Spring의 @PreDestroy와 함께 사용
    public void destroy() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}