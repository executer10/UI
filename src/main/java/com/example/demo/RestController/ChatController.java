//ChatController.java
package com.example.demo.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.component.AgentService;
import com.example.demo.dto.ChatRequestDTO;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatRequestDTO request) {
        return agentService.processUserInput(
                request.getMessage(),
                request.getSessionId(),
                request.getModel());
    }
}