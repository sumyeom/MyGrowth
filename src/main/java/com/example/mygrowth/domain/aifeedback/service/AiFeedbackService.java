package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiFeedbackService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiFeedbackService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    public WeeklyFeedbackResponse generateWeeklyFeedback(UserWeeklyData data) {
        //요일별 문자열 반환
        String daily = data.weeklyStats().dailySuccess().entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .reduce((a,b) -> a + "," + b)
                .orElse("");

        String prompt = """
                너는 생활 루틴 코치야.
                주어진 데이터를 보고 사용자에게 짧은 피드백을 1개 제공해.
                항상 같은 표현을 반복하지 말고, 다양한 톤(격려형, 분석형, 실천형 등)을 랜덤하게 선택해줘.
                
                [입력 데이터]
                성공률: %d%%
                가장 약한 요일: %s
                루틴 수: %d개
                """.formatted(
                data.weeklyStats().totalSuccessRate(),
                daily,
                data.weeklyStats().targetRoutineCount()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(WeeklyFeedbackResponse.class);
    }


}
