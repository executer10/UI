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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// [1] Ollama API 통신 핵심 서비스 - 텍스트 생성 요청 처리 및 스트리밍 응답 관리 [참조번호 : 2,5,7]
@Component
public class OllamaService {
    // [2-1] 로깅 시스템 도입
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    // [2-2] API 설정 상수
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // [2-3] 이름이 있는 스레드 풀로 변경하여 디버깅과 모니터링 용이성 향상
    private static final ExecutorService executorService = Executors.newCachedThreadPool(
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                private final ThreadGroup group = Thread.currentThread().getThreadGroup();

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(group, r, "ollama-response-processor-" + threadNumber.getAndIncrement(), 0);
                    if (t.isDaemon())
                        t.setDaemon(false);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

    // [2-4] 연결 타임아웃 및 읽기 타임아웃 설정 (밀리초)
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 60000;
    private static final int ASYNC_TIMEOUT = 90000; // 비동기 작업 타임아웃 설정 (90초)
    private static final int BUFFER_SIZE = 8192; // 버퍼 크기 최적화

    // [2-5] 문자열 처리용 상수 정의로 성능 최적화
    private static final char BACKSLASH = '\\';
    private static final char NEWLINE = '\n';
    private static final char TAB = '\t';
    private static final char QUOTE = '"';

    // [3] 스트리밍 응답 생성 메인 로직 - 실시간 텍스트 청크를 콜백으로 전달 [참조번호 : 4,5,6,7]
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        // [3-1] 입력 유효성 검증 추가
        if (message == null || message.trim().isEmpty()) {
            throw new OllamaRequestException("메시지는 null이거나 비어있을 수 없습니다");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new OllamaRequestException("모델은 null이거나 비어있을 수 없습니다");
        }
        if (onResponse == null) {
            throw new OllamaRequestException("응답 콜백은 null이 될 수 없습니다");
        }

        HttpURLConnection conn = null;
        try {
            // [3-2] 요청 바디 구성 - 필요한 경우에만 추가 옵션 포함 [참조번호 : 4]
            Map<String, Object> requestBody = createRequestBody(message, model, sessionId);
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            // [3-3] HTTP 연결 초기화 (성능 최적화) [참조번호 : 5]
            conn = setupConnection(jsonRequest);

            // [3-4] 요청 데이터 전송 (버퍼 최적화) [참조번호 : 6]
            sendRequest(conn, jsonRequest);

            // [3-5] HTTP 상태 코드 검증 추가
            int statusCode = conn.getResponseCode();
            if (statusCode >= 400) {
                String errorResponse = "";
                try (InputStream errorStream = conn.getErrorStream();
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
                    errorResponse = errorReader.lines().collect(Collectors.joining("\n"));
                } catch (Exception e) {
                    logger.warn("오류 응답 읽기 실패", e);
                }
                throw new OllamaConnectionException(
                        String.format("API 요청 실패: HTTP %d, 응답: %s", statusCode, errorResponse));
            }

            // [3-6] 스트리밍 응답 처리 (스트리밍 최적화) [참조번호 : 7]
            processStreamResponse(conn, onResponse);

        } catch (IOException e) {
            // [3-7] API 통신 예외 처리
            logger.error("API 통신 오류", e);
            throw new OllamaConnectionException("스트리밍 응답 생성 실패: " + e.getMessage(), e);
        } catch (OllamaServiceException e) {
            // [3-8] 이미 변환된 예외는 그대로 전파
            logger.error("Ollama 서비스 오류", e);
            throw e;
        } catch (Exception e) {
            // [3-9] 예상치 못한 예외에 대한 처리
            logger.error("예상치 못한 오류", e);
            throw new OllamaServiceException("예상치 못한 오류 발생: " + e.getMessage(), e);
        } finally {
            // [3-10] 리소스 해제 보장
            if (conn != null) {
                conn.disconnect(); // 연결 확실히 종료
                logger.debug("HTTP 연결 종료됨");
            }
        }
    }

    // [4] 요청 본문 생성 - 필요한 매개변수만 포함 [참조번호 : 3-2]
    private Map<String, Object> createRequestBody(String message, String model, String sessionId) {
        // [4-1] 초기 용량 지정으로 불필요한 rehash 방지
        Map<String, Object> requestBody = new HashMap<>(8);
        requestBody.put("model", model);
        requestBody.put("prompt", message);
        requestBody.put("stream", true);

        // [4-2] CMD와 유사한 성능을 위한 추가 매개변수
        requestBody.put("raw", true); // 원시 출력 활성화
        requestBody.put("num_predict", 4096); // 응답 토큰 수 증가

        // [4-3] 세션 ID가 있는 경우에만 추가
        if (sessionId != null && !sessionId.isEmpty()) {
            Map<String, Object> options = new HashMap<>(2);
            options.put("num_ctx", 4096);
            requestBody.put("options", options);
        }

        return requestBody;
    }

    // [5] HTTP 연결 설정 최적화 [참조번호 : 3-3]
    private HttpURLConnection setupConnection(String jsonRequest) throws IOException {
        // [5-1] 연결 생성 및 기본 설정
        URL url = new URL(OLLAMA_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        // [5-2] 헤더 설정
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Length", String.valueOf(jsonRequest.getBytes(StandardCharsets.UTF_8).length));

        // [5-3] 타임아웃 및 연결 옵션 설정
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        return conn;
    }

    // [6] 요청 전송 최적화 [참조번호 : 3-4]
    private void sendRequest(HttpURLConnection conn, String jsonRequest) throws IOException {
        // [6-1] 요청 데이터 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }
    }

    // [7] 스트림 응답 처리 최적화 - JSON 스트리밍 파서 사용 및 중복 파싱 제거 [참조번호 : 3-6]
    private void processStreamResponse(HttpURLConnection conn, Consumer<String> onResponse) throws IOException {
        // [7-1] 응답 스트림 설정
        try (InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8),
                        BUFFER_SIZE)) {

            // [7-2] 라인별 응답 처리
            String responseLine;
            while ((responseLine = br.readLine()) != null && !responseLine.isEmpty()) {
                final String line = responseLine;

                try {
                    // [7-3] JSON 파싱은 한 번만 수행
                    JsonNode jsonResponse = objectMapper.readTree(line);

                    // [7-4] done 필드 확인
                    boolean isDone = jsonResponse.has("done") && jsonResponse.get("done").asBoolean();

                    // [7-5] response 필드가 있으면 콜백 호출 - 별도 스레드에서 처리
                    if (jsonResponse.has("response")) {
                        final String response = jsonResponse.get("response").asText();
                        if (response != null && !response.isEmpty()) {
                            CompletableFuture.runAsync(() -> {
                                try {
                                    // [7-6] 응답 최적화 및 콜백 처리
                                    String optimized = optimizeResponseString(response);
                                    onResponse.accept(optimized);
                                } catch (Exception e) {
                                    logger.error("응답 처리 중 오류", e);
                                    // 치명적이지 않은 오류는 계속 진행
                                }
                            }, executorService);
                        }
                    }

                    // [7-7] 처리 완료 확인
                    if (isDone)
                        break;

                } catch (IOException e) {
                    // [7-8] 오류 로깅 추가
                    logger.warn("JSON 파싱 오류: {}", e.getMessage());
                    // 치명적이지 않은 오류는 계속 진행
                }
            }
        }
    }

    // [8] 문자열 치환 최적화 - StringBuilder 사용으로 변경 및 성능 개선 [참조번호 : 7-6]
    private String optimizeResponseString(String input) {
        // [8-1] 입력 검증
        if (input == null || input.isEmpty()) {
            return "";
        }

        // [8-2] 문자 교체가 필요한지 빠르게 확인
        if (input.indexOf(BACKSLASH) == -1) {
            return input; // 최적화: 변경 필요없으면 원본 반환
        }

        // [8-3] StringBuilder 초기 용량을 입력 길이로 설정하여 확장 방지
        StringBuilder sb = new StringBuilder(input.length());
        int len = input.length();

        // [8-4] 문자열 처리 최적화 루프
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == BACKSLASH && i + 1 < len) {
                char next = input.charAt(++i); // 미리 증가시켜 추가 연산 줄임
                switch (next) {
                    case 'n':
                        sb.append(NEWLINE);
                        break;
                    case 't':
                        sb.append(TAB);
                        break;
                    case '"':
                        sb.append(QUOTE);
                        break;
                    case '\\':
                        sb.append(BACKSLASH);
                        break;
                    default:
                        sb.append(c).append(next); // 알 수 없는 이스케이프는 그대로 유지
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // [9] 동기식 응답 생성 메서드 - 전체 응답을 문자열로 수집 후 반환 [참조번호 : 3]
    public String generateResponse(String message, String model, String sessionId) {
        // [9-1] 입력 유효성 검증
        if (message == null || model == null) {
            throw new OllamaRequestException("메시지와 모델은 null이 될 수 없습니다");
        }

        // [9-2] 초기 용량 설정 및 스레드 안전을 위한 StringBuffer 사용
        final StringBuffer fullResponse = new StringBuffer(4096); // 더 큰 초기 용량 지정

        try {
            // [9-3] synchronized 블록으로 스레드 안전성 보장
            generateResponseStream(message, model, sessionId, response -> {
                synchronized (fullResponse) {
                    fullResponse.append(response);
                }
            });

            return fullResponse.toString();
        } catch (Exception e) {
            // [9-4] 예외 로깅 및 변환
            logger.error("응답 생성 중 오류", e);
            if (e instanceof OllamaServiceException) {
                throw e;
            } else {
                throw new OllamaServiceException("응답 생성 중 오류: " + e.getMessage(), e);
            }
        }
    }

    // [10] 비동기 응답 생성 - CompletableFuture 반환 (타임아웃 처리 추가)
    public CompletableFuture<String> generateResponseAsync(String message, String model, String sessionId) {
        // [10-1] 입력 유효성 검증
        if (message == null || model == null) {
            CompletableFuture<String> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new OllamaRequestException("메시지와 모델은 null이 될 수 없습니다"));
            return failedFuture;
        }

        // [10-2] 응답 수집 버퍼 및 Future 생성
        CompletableFuture<String> future = new CompletableFuture<>();
        final StringBuffer fullResponse = new StringBuffer(4096);

        // [10-3] 별도 스레드에서 실행
        CompletableFuture.runAsync(() -> {
            try {
                generateResponseStream(message, model, sessionId, response -> {
                    synchronized (fullResponse) {
                        fullResponse.append(response);
                    }
                });
                future.complete(fullResponse.toString());
            } catch (Exception e) {
                // [10-4] 오류 로깅 및 예외 전파
                logger.error("비동기 응답 생성 중 오류", e);
                future.completeExceptionally(e);
            }
        }, executorService);

        // [10-5] 타임아웃 추가
        return future.orTimeout(ASYNC_TIMEOUT, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    // [10-6] 타임아웃 및 예외 처리
                    if (ex instanceof TimeoutException) {
                        logger.error("비동기 응답 생성 타임아웃 ({}ms)", ASYNC_TIMEOUT);
                        return "Ollama 응답 생성 타임아웃 (" + (ASYNC_TIMEOUT / 1000) + "초)";
                    }
                    // 다른 예외는 그대로 전파
                    if (ex instanceof CompletionException && ex.getCause() != null) {
                        logger.error("비동기 응답 생성 실패", ex.getCause());
                        throw new OllamaServiceException("비동기 응답 생성 실패: " + ex.getCause().getMessage(), ex.getCause());
                    }
                    logger.error("비동기 응답 생성 중 예상치 못한 오류", ex);
                    throw new OllamaServiceException("예상치 못한 오류: " + ex.getMessage(), ex);
                });
    }

    // [11] 애플리케이션 종료 시 실행자 서비스 종료 - 개선된 종료 처리
    public void shutdown() {
        // [11-1] 서비스 상태 확인
        if (executorService != null && !executorService.isShutdown()) {
            try {
                // [11-2] 정상 종료 시도
                logger.info("ExecutorService 종료 시작");
                executorService.shutdown();

                // [11-3] 지연된 작업 완료 기다리기
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("일부 작업이 5초 내에 완료되지 않아 강제 종료합니다");
                    executorService.shutdownNow();

                    // [11-4] 강제 종료 후 추가 대기
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        logger.error("ExecutorService가 완전히 종료되지 않았습니다");
                    }
                }

                logger.info("ExecutorService 정상 종료됨");
            } catch (Exception e) {
                // [11-5] 종료 중 예외 처리
                logger.error("ExecutorService 종료 중 오류", e);
                executorService.shutdownNow();
            }
        }
    }

    // [12] 커스텀 예외 클래스 계층 정의

    /**
     * [12-1] Ollama 서비스 관련 기본 예외 클래스
     */
    public static class OllamaServiceException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OllamaServiceException(String message) {
            super(message);
        }

        public OllamaServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * [12-2] Ollama API 연결 관련 예외
     */
    public static class OllamaConnectionException extends OllamaServiceException {
        private static final long serialVersionUID = 1L;

        public OllamaConnectionException(String message) {
            super(message);
        }

        public OllamaConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * [12-3] Ollama API 요청 관련 예외 (잘못된 입력 매개변수 등)
     */
    public static class OllamaRequestException extends OllamaServiceException {
        private static final long serialVersionUID = 1L;

        public OllamaRequestException(String message) {
            super(message);
        }

        public OllamaRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * [12-4] Ollama API 응답 파싱 관련 예외
     */
    public static class OllamaResponseParsingException extends OllamaServiceException {
        private static final long serialVersionUID = 1L;

        public OllamaResponseParsingException(String message) {
            super(message);
        }

        public OllamaResponseParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}