package com.rpl.splurge.service;


import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class SpendAnalysis {

    private static final String API_KEY = "XtLdcJ9CjRZRE6u6d14QymytNMKE2aIg";
    private static final String API_URL = "https://api.mistral.ai/v1/chat/completions";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analysisAndSpend(double totalSpent, double budget,
                                double remaining, Map<String, Double> byCategory) {

    	StringBuilder prompt = new StringBuilder();
    	prompt.append("You are a sharp, friendly financial coach for Indian millennials. ");
    	prompt.append("Analyze this person's monthly spending and give exactly 3 short tips to save money. ");
    	prompt.append("Format your response EXACTLY like this — no markdown, no bold, no asterisks:\n\n");
    	prompt.append("VERDICT: [one punchy sentence about their overall spending]\n\n");
    	prompt.append("TIP 1: [specific actionable tip based on their highest category]\n");
    	prompt.append("TIP 2: [specific actionable tip based on their second category]\n");
    	prompt.append("TIP 3: [one general saving tip relevant to their spending]\n\n");
    	prompt.append("SAVE THIS MONTH: ₹[realistic amount they can save]\n\n");
    	prompt.append("Keep each line under 20 words. Use Indian context. No markdown formatting.\n\n");
    	prompt.append("Their spending this month:\n");
    	prompt.append("Total spent: ₹").append(totalSpent).append("\n");
    	prompt.append("Budget: ₹").append(budget).append("\n");
    	prompt.append("Remaining: ₹").append(remaining).append("\n");
    	prompt.append("By category:\n");
    	byCategory.forEach((cat, amt) ->
    	    prompt.append("- ").append(cat).append(": ₹").append(amt).append("\n")
    	);
    	prompt.append("\nGive the analysis now:");

        String requestBody = """
            {
                "model": "mistral-small-latest",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ],
                "max_tokens": 300
            }
            """.formatted(prompt.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n"));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Mistral raw response: " + response.body());

            Map<?, ?> responseMap = objectMapper.readValue(response.body(), Map.class);

            // Check for error
            if (responseMap.containsKey("error")) {
                System.out.println("Mistral error: " + responseMap.get("error"));
                return "AI is taking a break right now. Try again in a minute! ⏳";
            }

            var choices = (java.util.List<?>) responseMap.get("choices");
            var first = (Map<?, ?>) choices.get(0);
            var message = (Map<?, ?>) first.get("message");
            return message.get("content").toString();

        } catch (Exception e) {
            System.out.println("Mistral error: " + e.getMessage());
            return "Could not generate roast. Your spending is either too good or too tragic to analyze. 💀";
        }
    }
}