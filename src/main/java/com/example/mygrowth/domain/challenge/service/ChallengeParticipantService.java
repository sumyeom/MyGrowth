package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeParticipantResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.repository.ChallengeParticipantRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.example.mygrowth.domain.challenge.enums.ChallengeStatus.ONGOING;

@Service
@RequiredArgsConstructor
public class ChallengeParticipantService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;

    /**
     * 챌린지 참여
     * @param id 챌린지 id
     * @param loginUser 로그인 유저
     * @return 챌린지 참여 dto
     */
    @Transactional
    public ChallengeParticipantResponseDto joinChallenge(Long id, User loginUser) {
        // 챌린지 조회
        Challenge challenge =  challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 이미 참여한 챌린지인지 확인
        if(challengeParticipantRepository.existsByChallengeIdAndUserId(challenge.getId(), loginUser.getId())){
            throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
        }

        // 정원 확인
        if(challenge.getCurrentParticipants() >= challenge.getMaxParticipants()){
            throw new ApiException(ErrorCode.OVER_PARTICIPANTS);
        }


        // 챌린지 참여
        ChallengeParticipant challengeParticipant = new ChallengeParticipant(
                LocalDate.now(),
                ONGOING,
                loginUser,
                challenge
        );

        // 챌린지 참여 저장
        ChallengeParticipant saved = challengeParticipantRepository.save(challengeParticipant);

        // 참여자 추가
        challenge.incrementParticipants();

        return ChallengeParticipantResponseDto.from(saved);
    }
}
