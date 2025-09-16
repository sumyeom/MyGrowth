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
import org.hibernate.StaleObjectStateException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.example.mygrowth.domain.challenge.enums.ChallengeStatus.ONGOING;

@Service
@RequiredArgsConstructor
public class ChallengeParticipantService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "challenge:lock:";

    /**
     *
     * @param challengeId
     * @param userId
     */
    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    public ChallengeParticipantResponseDto joinChallengeWithRedisLockOptimistic(Long challengeId, Long userId) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + challengeId);

        try{
            // 최대 5초 동안 락 대기 ,10초 동안 락 유지
            if (lock.tryLock(5, 2, TimeUnit.SECONDS)) {
                // 챌린지 중복 먼저 체크
                if (challengeParticipantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
                    throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

                Challenge challenge = challengeRepository.findById(challengeId)
                        .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

                // 정원 체크
                if (challenge.getCurrentParticipants() >= challenge.getMaxParticipants()) {
                    throw new ApiException(ErrorCode.OVER_PARTICIPANTS);
                }

                ChallengeParticipant participant = new ChallengeParticipant(
                        LocalDate.now(), ONGOING, user, challenge
                );

                // 챌린지 참여 저장
                ChallengeParticipant saved = challengeParticipantRepository.save(participant);

                challenge.incrementParticipants();

                // Version 충돌 감지
                challengeRepository.save(challenge);

                return ChallengeParticipantResponseDto.from(saved);

            } else{
                System.out.println("User " + userId + " 락 대기 실패");
                throw new ApiException(ErrorCode.CONCURRENT_UPDATE_FAILED);
            }
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        } finally{
            // 락 해제
            if(lock.isLocked()){
                lock.unlock();
            }
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

    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 10,  // 재시도 횟수 증가
            backoff = @Backoff(
                    delay = 100,
                    multiplier = 1.5,  // 지수 백오프
                    random = true      // 랜덤 지연
            )
    )
    public void joinChallengeWithOptimisticLock(Long challengeId, Long userId) {
        // 챌린지 중복 먼저 체크
        if (challengeParticipantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
            throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 정원 체크
        if (challenge.getCurrentParticipants() >= challenge.getMaxParticipants()) {
            throw new ApiException(ErrorCode.OVER_PARTICIPANTS);
        }

        ChallengeParticipant participant = new ChallengeParticipant(
                LocalDate.now(), ONGOING, user, challenge
        );
        challengeParticipantRepository.save(participant);

        challenge.incrementParticipants();

        // Version 충돌 감지
        challengeRepository.save(challenge);
    }


    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    public void joinChallengeWithRedisLock(Long challengeId, Long userId) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + challengeId);

        try{
            // 최대 5초 동안 락 대기 ,10초 동안 락 유지
            if (lock.tryLock(5, 2, TimeUnit.SECONDS)) {
                // 챌린지 중복 먼저 체크
                if (challengeParticipantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
                    throw new ApiException(ErrorCode.ALREADY_JOIN_CHALLENGE);
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

                Challenge challenge = challengeRepository.findById(challengeId)
                        .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

                // 정원 체크
                if (challenge.getCurrentParticipants() >= challenge.getMaxParticipants()) {
                    throw new ApiException(ErrorCode.OVER_PARTICIPANTS);
                }

                ChallengeParticipant participant = new ChallengeParticipant(
                        LocalDate.now(), ONGOING, user, challenge
                );
                challengeParticipantRepository.save(participant);

                challenge.incrementParticipants();

                // Version 충돌 감지
                challengeRepository.save(challenge);
            } else{
                System.out.println("User " + userId + " 락 대기 실패");
                throw new ApiException(ErrorCode.CONCURRENT_UPDATE_FAILED);
            }
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        } finally{
            // 락 해제
            if(lock.isLocked()){
                lock.unlock(); 
            }
        }
    }

    @Recover
    public void recover(OptimisticLockException e, Long challengeId, Long userId) {
        throw new ApiException(ErrorCode.CONCURRENT_UPDATE_FAILED);
    }
}
