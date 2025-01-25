package com.UI.utills;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CS_Executor {
    // C# 실행 파일 명령어
    private static final String CSHARP_COMMAND = "dotnet";

    // 디버그 모드 설정
    private static final boolean DEBUG_MODE = true;
    
    // 실행 제한 시간 (초)
    private static final int TIMEOUT_SECONDS = 30;
    
    // 초기 버퍼 크기
    private static final int INITIAL_BUFFER_SIZE = 1024;
    
    // 명령어 리스트의 초기 용량
    private static final int COMMAND_LIST_INITIAL_CAPACITY = 3;

    // JSON 데이터 처리를 위한 ObjectMapper 설정
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    // JSON 배열을 파싱할 때 사용하는 타입 참조
    private static final TypeReference<List<Map<String, Object>>> JSON_TYPE_REFERENCE_LIST = 
        new TypeReference<List<Map<String, Object>>>() {};

    // JSON 객체를 파싱할 때 사용하는 타입 참조
    private static final TypeReference<Map<String, Object>> JSON_TYPE_REFERENCE_OBJECT = 
        new TypeReference<Map<String, Object>>() {};

    // C# 실행 환경 설정
    private static final String DOTNET_SETUP = String.join("\n",
            "Console.OutputEncoding = System.Text.Encoding.UTF8;",
            "Console.InputEncoding = System.Text.Encoding.UTF8;");
    
    // 동시 실행을 제어하기 위한 락
    private static final ReentrantLock LOCK = new ReentrantLock();

    // C# 실행 파일 경로
    private final String csFilePath;
    
    // 실행할 C# 함수 이름
    private final String csFunctionName;

    /**
     * CS_Executor 생성자
     */
    public CS_Executor(String csFilePath, String csFunctionName) {
        if (csFilePath == null || csFunctionName == null) {
            throw new IllegalArgumentException("C# 파일 경로와 함수 이름은 null일 수 없습니다.");
        }
        this.csFilePath = csFilePath.replace("\\", "/");
        this.csFunctionName = csFunctionName;
    }
    
    /**
     * C# 함수를 실행하고 결과를 반환합니다.
     */
    public Object executeFunction(Object... args) {
        long startTime = System.nanoTime();
        LOCK.lock();
        Process process = null;
        try {
            validateFile();
            
            try {
                process = executeProcess(buildCommand(args));
                String result = captureOutput(process);

                if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    return "실행 오류: 시간 초과";
                }

                if (process.exitValue() != 0) {
                    String errorOutput = captureError(process);
                    return "실행 오류: " + errorOutput;
                }

                return parseResultIfJson(result);
            } catch (Exception e) {
                return "실행 오류: " + e.getMessage();
            } finally {
                if (process != null) {
                    process.destroyForcibly();
                }
                cleanupTempFiles();
                if (DEBUG_MODE) {
                    long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                    System.out.println("[DEBUG] 함수 실행 시간: " + executionTime + "ms");
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 결과가 JSON 형식인 경우 파싱하여 Java 객체로 변환합니다.
     */
    private Object parseResultIfJson(String result) {
        String trimmed = result.trim();

        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return JSON_MAPPER.readValue(trimmed, JSON_TYPE_REFERENCE_LIST);
            } catch (Exception e) {
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] JSON 배열 파싱 실패: " + e.getMessage());
                }
                return result;
            }
        } else if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                return JSON_MAPPER.readValue(trimmed, JSON_TYPE_REFERENCE_OBJECT);
            } catch (Exception e) {
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] JSON 객체 파싱 실패: " + e.getMessage());
                }
                return result;
            }
        }
        return result;
    }
    
    /**
     * C# 실행 파일이 유효한지 검사합니다.
     */
    private void validateFile() {
        File file = new File(csFilePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("C# 파일 경로가 유효하지 않습니다: " + csFilePath);
        }
    }
    
    /**
     * 실행할 명령어 리스트를 생성합니다.
     */
    private List<String> buildCommand(Object... args) {
        List<String> command = new ArrayList<>(COMMAND_LIST_INITIAL_CAPACITY);
        command.add(CSHARP_COMMAND);
        command.add(csFilePath);
        command.add(csFunctionName);
        
        for (Object arg : args) {
            if (arg instanceof String) {
                command.add("\"" + ((String) arg).replace("\"", "\\\"") + "\"");
            } else {
                command.add(arg.toString());
            }
        }
        
        if (DEBUG_MODE) {
            System.out.println("[DEBUG] 실행할 명령어: " + command);
        }
        return command;
    }
    
    /**
     * ProcessBuilder를 사용하여 C# 프로세스를 실행합니다.
     */
    private Process executeProcess(List<String> command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command)
                .redirectErrorStream(true);
        
        // 환경 변수 설정
        Map<String, String> env = processBuilder.environment();
        env.put("DOTNET_ENVIRONMENT", "Production");
        env.put("DOTNET_CLI_TELEMETRY_OPTOUT", "1");
        env.put("DOTNET_NOLOGO", "true");
        
        return processBuilder.start();
    }
    
    /**
     * 프로세스의 출력을 읽어 문자열로 반환합니다.
     */
    private String captureOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder(INITIAL_BUFFER_SIZE);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] 출력: " + line);
                }
            }
        }
        return output.toString().trim();
    }

    /**
     * 프로세스의 에러 출력을 읽어 문자열로 반환합니다.
     */
    private String captureError(Process process) throws Exception {
        StringBuilder error = new StringBuilder(INITIAL_BUFFER_SIZE);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line).append('\n');
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] 에러: " + line);
                }
            }
        }
        return error.toString().trim();
    }

    /**
     * 임시 파일들을 정리합니다.
     */
    private void cleanupTempFiles() {
        // bin과 obj 폴더의 경로
        File binDir = new File(new File(csFilePath).getParent(), "bin");
        File objDir = new File(new File(csFilePath).getParent(), "obj");
        
        // 임시 폴더 삭제
        if (binDir.exists()) deleteDirectory(binDir);
        if (objDir.exists()) deleteDirectory(objDir);
    }

    /**
     * 재귀적으로 디렉토리를 삭제합니다.
     */
    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }
}