package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiFeedbackService {
    private final ChatClient chatClient;

    public AiFeedbackService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public WeeklyFeedbackResponse generateWeeklyFeedback(UserWeeklyData data) {
        // 일자별 성공 문자열
        String daily = data.weeklyStats().dailySuccess().entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("없음");

        String prompt = """
        너는 생활 루틴 코치야.
        이번 주 데이터를 기반으로 사용자에게 짧고 따뜻한 격려 메시지 한 문장만 생성해.
        구체적인 수치 언급은 가능하지만 너무 딱딱하지 않게 해.
        
        [입력 데이터]
        성공률: %d%%
        요일별 성공: %s
        루틴 수: %d개
        
        출력 형식 (JSON):
        {
          "message": "이번 주에도 열심히 했네요! 특히 주말의 성과가 돋보여요."
        }
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
