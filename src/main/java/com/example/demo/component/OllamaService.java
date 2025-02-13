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
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// [1] Ollama API 통신 핵심 서비스 - 텍스트 생성 요청 처리 및 스트리밍 응답 관리 [참조번호 : 2,5,7]
@Service
public class OllamaService {

    // [2] API 설정 상수
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // [3] 스트리밍 응답 생성 메인 로직 - 실시간 텍스트 청크를 콜백으로 전달
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        try {
            // [4] 요청 바디 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", message);
            requestBody.put("stream", true);

            // [5] HTTP 연결 초기화
            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // [6] 요청 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input);
                os.flush();
            }

            // [7] 스트리밍 응답 처리
            try (InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    if (!responseLine.isEmpty()) {
                        // JSON 파싱 (필요에 따라 JsonParser를 사용하여 스트리밍 방식으로 전환 가능)
                        JsonNode jsonResponse = objectMapper.readTree(responseLine);
                        if (jsonResponse.has("response")) {
                            String response = jsonResponse.get("response").asText();
                            // 문자열 치환 최적화: 불필요한 임시 객체 생성을 줄이기 위해 단일 메서드로 처리
                            response = optimizeResponseString(response);
                            onResponse.accept(response);
                        }
                        if (jsonResponse.has("done") && jsonResponse.get("done").asBoolean()) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("스트리밍 응답 생성 실패: " + e.getMessage(), e);
        }
    }

    // 문자열 치환을 하나의 메서드로 묶어 최적화
    private String optimizeResponseString(String input) {
        // 치환 순서나 로직을 조정하여 불필요한 중간 객체 생성을 줄임
        return input.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    // [8] 동기식 응답 생성 메서드 - 전체 응답을 문자열로 수집 후 반환 [참조번호 : 3]
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder();
        generateResponseStream(message, model, sessionId, response -> fullResponse.append(response));
        return fullResponse.toString();
    }
}
