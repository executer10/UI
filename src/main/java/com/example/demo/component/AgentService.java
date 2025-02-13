package com.example.demo.component;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.example.demo.component.AgentParts.DatabaseManager;
import com.example.demo.component.AgentParts.HistoryManager;
import com.example.demo.component.AgentParts.ProjectLoader;

import jakarta.annotation.PostConstruct;

@Component
public class AgentService {
    private final OllamaService ollamaService;
    private final ProjectLoader projectLoader;
    private final DatabaseManager databaseManager;
    private final HistoryManager historyManager;
    private final UserInputProcessor userInputProcessor;

    String projectContext;
    String databaseContext;

    public AgentService(OllamaService ollamaService, ProjectLoader projectLoader,
            DatabaseManager databaseManager, HistoryManager historyManager) {
        this.ollamaService = ollamaService;
        this.projectLoader = projectLoader;
        this.databaseManager = databaseManager;
        this.historyManager = historyManager;
        this.userInputProcessor = new UserInputProcessor();
    }

    @PostConstruct
    public void initialize() {
        projectContext = projectLoader.loadProjectStructure();
        databaseContext = databaseManager.loadDatabaseSchema();
    }

    public void processUserInputStream(String userInput, String sessionId, String model, Consumer<String> onResponse) {
        userInputProcessor.process(userInput, sessionId, model, onResponse);
    }

    public String processUserInput(String userInput, String sessionId, String model) {
        StringBuilder response = new StringBuilder();
        processUserInputStream(userInput, sessionId, model, response::append);
        return response.toString();
    }

    public void clearConversation(String sessionId) {
        historyManager.clearHistory(sessionId);
    }

    private class UserInputProcessor {
        private static final String SYSTEM_PROMPT = """
                Context Information:
                Project Information: %s
                Database Information: %s

                Previous Conversation: %s

                Current Question: %s
                """;

        public void process(String userInput, String sessionId, String model, Consumer<String> onResponse) {
            try {
                String context = buildContext(userInput);
                String prompt = String.format(SYSTEM_PROMPT,
                        context,
                        databaseContext + getQueryResults(userInput),
                        historyManager.findRelevantHistory(userInput, sessionId, model),
                        userInput);

                generateResponse(prompt, model, sessionId, onResponse);
                updateHistory(sessionId, userInput);
            } catch (Exception e) {
                onResponse.accept("Error occurred: " + e.getMessage());
            }
        }

        private String buildContext(String input) {
            return String.join("\n\n", projectLoader.findSimilarFiles(input).stream()
                    .limit(5)
                    .map(f -> "File: " + f + "\n" + projectLoader.getFileContent(f))
                    .toList());
        }

        private String getQueryResults(String input) {
            StringBuilder results = new StringBuilder("\n\nDatabase Query Results:");
            var tables = databaseManager.findRelatedTables(input);

            if (tables.isEmpty()) {
                return results.append("\nNo relevant data found").toString();
            }

            for (String table : tables) {
                results.append("\n\n").append(getTableData(table));
            }

            return results.toString();
        }

        private String getTableData(String table) {
            long count = databaseManager.getTableCount(table);
            DatabaseManager.QueryResult queryResult = databaseManager.executeSelectQuery(
                    String.format("SELECT * FROM %s ORDER BY 1 DESC", table));

            StringBuilder result = new StringBuilder(String.format("Table '%s':\n- Total records: %d\n", table, count));

            if (queryResult.isSuccess() && queryResult.getData() != null) {
                result.append("Recent data samples:\n");
                queryResult.getData().forEach(row -> {
                    result.append("- ");
                    row.forEach((column, value) -> result.append(String.format("%s=%s, ", column, value)));
                    result.append("\n");
                });
            }

            return result.toString();
        }

        private void generateResponse(String prompt, String model, String sessionId, Consumer<String> onResponse) {
            ollamaService.generateResponseStream(prompt, model, sessionId, response -> {
                if (response != null && !response.trim().isEmpty()) {
                    onResponse.accept(response);
                }
            });
        }

        private void updateHistory(String sessionId, String userInput) {
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("user", userInput));
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("assistant", "Response completed"));
        }
    }
}