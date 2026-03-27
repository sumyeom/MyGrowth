package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeParticipantResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.repository.ChallengeParticipantRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.mygrowth.domain.challenge.enums.ChallengeStatus.ONGOING;

@Service
@RequiredArgsConstructor
public class ChallengeParticipantService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final UserRepository userRepository;

    /**
     * 챌린지 참여
     * @param challengeId 챌린지 id
     * @param userId 로그인한 id
     */
    @Transactional
    @Retryable(
            retryFor = {
                    ObjectOptimisticLockingFailureException.class,
                    OptimisticLockException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 1.5, random = true)
    )
    public ChallengeParticipantResponseDto joinChallenge(Long challengeId, Long userId) {
        if (challengeParticipantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
            throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (challenge.getCurrentParticipants() >= challenge.getMaxParticipants()) {
            throw new ApiException(ErrorCode.OVER_PARTICIPANTS);
        }

        ChallengeParticipant participant = new ChallengeParticipant(
                LocalDate.now(), ONGOING, user, challenge
        );

        try {
            ChallengeParticipant saved = challengeParticipantRepository.save(participant);
            challenge.incrementParticipants();
            challengeRepository.save(challenge);
            return ChallengeParticipantResponseDto.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
        }
    }


    // synchronized 메서드별 락
    @Transactional
    public synchronized void joinChallengeWithSync(Long id, Long loginUserId) {
        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 챌린지 조회
        Challenge challenge =  challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 이미 참여한 챌린지인지 확인
        if(challengeParticipantRepository.existsByChallengeIdAndUserId(challenge.getId(), loginUserId)){
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
                user,
                challenge
        );

        // 챌린지 참여 저장
        ChallengeParticipant saved = challengeParticipantRepository.save(challengeParticipant);

        // 참여자 추가
        challenge.incrementParticipants();

        //return ChallengeParticipantResponseDto.from(saved);
    }

    // 챌린지별 락
    private final ConcurrentHashMap<Long, Object> challengeLocks = new ConcurrentHashMap<>();

    @Transactional
    public void joinChallengeWithChallengeSpecificLock(Long id, Long loginUserId) {

        Object lock = challengeLocks.computeIfAbsent(id,k -> new Object());

        synchronized (lock) {
            User user = userRepository.findById(loginUserId)
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            // 챌린지 조회
            Challenge challenge =  challengeRepository.findById(id)
                    .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

            // 이미 참여한 챌린지인지 확인
            if(challengeParticipantRepository.existsByChallengeIdAndUserId(challenge.getId(), loginUserId)){
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
                    user,
                    challenge
            );

            // 챌린지 참여 저장
            ChallengeParticipant saved = challengeParticipantRepository.save(challengeParticipant);

            // 참여자 추가
            challenge.incrementParticipants();

            //return ChallengeParticipantResponseDto.from(saved);
        }

    }

    @Transactional
    public void joinChallengeWithPessimisticLock(Long id, Long loginUserId) {
        // 비관적 락
        // 챌린지 조회
        Challenge challenge =  challengeRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 이미 참여한 챌린지인지 확인
        if(challengeParticipantRepository.existsByChallengeIdAndUserId(challenge.getId(), loginUserId)){
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
                user,
                challenge
        );

        // 챌린지 참여 저장
        ChallengeParticipant saved = challengeParticipantRepository.save(challengeParticipant);

        // 참여자 추가
        challenge.incrementParticipants();

    }

    @Recover
    public ChallengeParticipantResponseDto recoverOptimisticLock(Exception e, Long challengeId, Long userId) {
        throw new ApiException(ErrorCode.CONCURRENT_UPDATE_FAILED);
    }
}
