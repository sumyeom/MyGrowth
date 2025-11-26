package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeLogRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeLogResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.entity.ChallengeLog;
import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.enums.ChallengeStatus;
import com.example.mygrowth.domain.challenge.repository.ChallengeLogRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeParticipantRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.example.mygrowth.domain.challenge.entity.QChallengeLog.challengeLog;

@Service
@RequiredArgsConstructor
public class ChallengeLogService {
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeLogRepository challengeLogRepository;

    /**
     * 챌린지 인증
     * @param id 챌린지 참여 id
     * @param requestDto 인증 내용
     * @param loginUser 로그인 유저
     * @return 인증 내용 dto
     */
    @Transactional
    public ChallengeLogResponseDto createChallengeLog(Long id, ChallengeLogRequestDto requestDto, User loginUser) {
        // 챌린지 참여 조회
        ChallengeParticipant participant = challengeParticipantRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_PARTICIPANT_NOT_FOUND));

        Challenge challenge = participant.getChallenge();

        // 이미 완료한 챌린지이면 throw
        if(participant.getStatus().equals(ChallengeStatus.COMPLETED)){
            throw new ApiException(ErrorCode.SUCCESS_CHALLENGE);

        }

        // 이미 인증한 챌린지인지 확인(하루에 한번만 인증 가능)
        if(challengeLogRepository.existsByChallengeParticipantAndDate(participant, LocalDate.now())){
            throw new ApiException(ErrorCode.ALREADY_LOGGED_TODAY);
        }

        ChallengeLog challengeLog = ChallengeLog.builder()
                .date(LocalDate.now())
                .content(requestDto.getContent())
                .challengeParticipant(participant)
                .build();

        ChallengeLog savedChallengeLog = challengeLogRepository.save(challengeLog);

        // 챌린지 참여에 logCount 증가
        participant.increaseLogCount();

        // 인증한 횟수와 challenge의 targetCount랑 비교
        if(challenge.getTargetCount() <= participant.getLogCount()){
            participant.updateStatus(ChallengeStatus.COMPLETED);
        }

        return new ChallengeLogResponseDto(
                savedChallengeLog.getId(),
                savedChallengeLog.getChallengeParticipant().getId(),
                savedChallengeLog.getDate(),
                savedChallengeLog.getContent()
        );
    }
}
