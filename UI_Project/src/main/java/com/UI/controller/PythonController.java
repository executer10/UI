package com.UI.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.UI.utills.PythonExecutor;

// 스프링에서 파이썬 코드를 실행하는 컨트롤러
@Controller
public class PythonController {

    // 계산기 화면을 보여주는 메서드
    @GetMapping("/projectPython")
    public String showCalculatorPage() {
        return "/Projects/projectPython"; // projectPython.jsp로 이동
    }

    // 사용자가 입력한 숫자를 계산
    @PostMapping("/projectPython/calculate")
    public String calculateNumbers(
    		@RequestParam("num1") int num1,
            @RequestParam("num2") int num2, 
            Model model) {
    	// 파이썬 파일 경로, 모듈 이름, 함수 이름을 상수로 정의
        final String PYTHON_FILE_PATH = "C:/My/Spring_Example/UI_Project/src/main/webapp/resources/Python";
        final String PYTHON_MODULE_NAME = "app";
        final String PYTHON_FUNCTION_NAME = "testFunc";
        
        try {
            // PythonExecutor를 사용하여 파이썬 코드 실행
            PythonExecutor executor = new PythonExecutor(PYTHON_FILE_PATH, PYTHON_MODULE_NAME);
            Object result = executor.executeFunction(
            		PYTHON_FUNCTION_NAME, 
            		num1, 
            		num2
            );

            // 결과를 모델에 추가
            model.addAttribute("result", "계산 결과: " + result);
        } catch (Exception e) {
            model.addAttribute("result", "오류가 발생했습니다: " + e.getMessage());
        }

        return "/Projects/projectPython"; // 결과를 보여줄 화면으로 이동
    }
    
    @GetMapping("/dataAnalysis")
    public String analyze() {
        return "/Projects/projectForecastDashboard"; // projectPython.jsp로 이동
    }
    
    @PostMapping("/dataAnalysis/analyze")
    @ResponseBody
    public Map<String, Object> analyzeData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("featureColumn") String featureColumn,
            @RequestParam("targetColumn") String targetColumn) {
        Map<String, Object> response = new HashMap<>();
        
        final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploaded_files/";
        final String PYTHON_FILE_PATH = "C:/My/Spring_Example/UI_Project/src/main/webapp/resources/Python";
        final String PYTHON_MODULE_NAME = "Graph";
        final String PYTHON_FUNCTION_NAME = "analyze_data";
        
        try {
            // 업로드된 파일 저장
            String fileName = file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;

            // 디렉토리가 없으면 생성
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 파일 저장
            File dest = new File(filePath);
            file.transferTo(dest);
            
            // Python 분석 실행
            PythonExecutor executor = new PythonExecutor(PYTHON_FILE_PATH, PYTHON_MODULE_NAME);
            Object result = executor.executeFunction(
                PYTHON_FUNCTION_NAME, 
                filePath,
                featureColumn,
                targetColumn
            );
            
            if (result instanceof String && ((String) result).startsWith("Error:")) {
                response.put("status", "error");
                response.put("message", ((String) result).substring(6));
            } else {
                response.put("status", "success");
                response.put("graphPath", "data:image/png;base64," + result);
            }
            
            // 임시 파일 삭제
            dest.delete();
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    @GetMapping("/GasDashboard")
    public String GasDashboard() {
        return "/Projects/projectGasDashboard"; // projectPython.jsp로 이동
    }
    

    @GetMapping("/GasDashboard/getGasSupplyData")
    @ResponseBody
    public Map<String, Object> getGasSupplyData() {
        final String PYTHON_PATH = "C:/My/Spring_Example/UI_Project/src/main/webapp/resources/Python";
        final String PYTHON_MODULE = "GasDashboard";
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            PythonExecutor executor = new PythonExecutor(PYTHON_PATH, PYTHON_MODULE);
            Object result = executor.executeFunction("main");
            
            response.put("status", "success");
            response.put("data", result);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    @RequestMapping("/ElectricalFireStatistics")
    public String getFireStatistics(Model model) {
        PythonExecutor executor = new PythonExecutor(
            "C:\\My\\Spring_Example\\UI_Project\\src\\main\\webapp\\resources\\Python",
            "ElectricalFireStatisticsDashboard"
        );
        
        Object result = executor.executeFunction("main");
        model.addAttribute("fireData", result);
        
        return "/Projects/ElectricalFireStatisticsDashboard";
    }
}
