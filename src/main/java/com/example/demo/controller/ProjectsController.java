package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.component.OllamaManager;
import com.example.demo.utils.PythonExecutor;

@Controller
public class ProjectsController {

	@Autowired
	private OllamaManager ollamaManager;

	@GetMapping("/projects")
	public String Projects() {
		return "projects";
	}

	@GetMapping("/deepSeek")
	public String deepSeek(Model model) throws IOException {
		ollamaManager.startOllamaIfNotRunning();
		return "/project/deepSeek";
	}

	@GetMapping("/3D_Bouncing_Sphere")
	public String BouncingSphere() {
		return "/project/3D_Bouncing_Sphere";
	}

	@GetMapping("/GasDashboard")
	public String GasDashboard() {
		return "/project/GasDashboard";
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

}
