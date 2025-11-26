package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeUserRoutineRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeUserRoutineResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.entity.ChallengeUserRoutine;
import com.example.mygrowth.domain.challenge.repository.ChallengeParticipantRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeUserRoutineRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeUserRoutineService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeUserRoutineRepository challengeUserRoutineRepository;

    /**
     * 유저의 챌린지 루틴 생성
     * @param id 챌린지 id
     * @param requestDto 루틴 dto
     * @param loginUser 로그인한 유저
     * @return 생성 루틴 dto
     */
    public ChallengeUserRoutineResponseDto createChallengeUserRoutine(Long id, ChallengeUserRoutineRequestDto requestDto, User loginUser) {
        //챌린지 조회
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 유저가 참여한 이력 확인
        ChallengeParticipant participant = challengeParticipantRepository
                .findByChallenge_IdAndUser_Id(challenge.getId(), loginUser.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_PARTICIPANT_NOT_FOUND));


        // 루틴 생성
        ChallengeUserRoutine challengeUserRoutine = ChallengeUserRoutine.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .challengeParticipant(participant)
                .build();

        ChallengeUserRoutine savedRoutine = challengeUserRoutineRepository.save(challengeUserRoutine);
        return new ChallengeUserRoutineResponseDto(
                savedRoutine.getId(),
                savedRoutine.getTitle(),
                savedRoutine.getDescription()
        );
    }

    /**
     * 챌린지 루틴 조회
     * @param id 챌린지 id
     * @param loginUser 로그인 유저
     * @return 챌린지 루틴 dto
     */
    public ChallengeUserRoutineResponseDto getChallengeUserRoutine(Long id, User loginUser) {
        //챌린지 조회
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 유저가 참여한 이력 확인
        ChallengeParticipant participant = challengeParticipantRepository
                .findByChallenge_IdAndUser_Id(challenge.getId(), loginUser.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_PARTICIPANT_NOT_FOUND));

        // 챌린지 유저 루틴
        ChallengeUserRoutine findRoutine = challengeUserRoutineRepository.findByChallengeParticipant(participant)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_PARTICIPANT_NOT_FOUND));

        return new  ChallengeUserRoutineResponseDto(
                findRoutine.getId(),
                findRoutine.getTitle(),
                findRoutine.getDescription()
        );
    }
}
