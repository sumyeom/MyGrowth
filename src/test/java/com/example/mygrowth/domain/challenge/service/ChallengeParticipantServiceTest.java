package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeParticipantResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.repository.ChallengeParticipantRepository;
import com.example.mygrowth.domain.challenge.repository.ChallengeRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.repository.UserRepository;
import com.example.mygrowth.global.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@SpringBootTest // 전체 스프링 컨텍스트 로딩
@ActiveProfiles("test")
class ChallengeParticipantServiceTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ChallengeParticipantService challengeParticipantService;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeParticipantRepository challengeParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= 실제 테스트 =================
    @Test
    @DisplayName("동시성 문제 체험 - 1")
    void 동시성_문제_1() throws InterruptedException{
        // Given : 챌린지 생성
        Challenge challenge = createTestChallenge("동시성 테스트 챌린지", 100);
        Challenge savedChallenge = challengeRepository.save(challenge);

        // 200명의 테스트 사용자 생성
        List<User> testUser = createTestUsers(200);

        System.out.println("=== 동시성 테스트 시작 ===");
        System.out.println("챌린지 : " + challenge.getTitle());
        System.out.println("최대 참여자 : " + challenge.getMaxParticipants());
        System.out.println("동시 참여 시도 : " + testUser.size() + "명");

        // When : 200명이 동시에 참여 시도
        int threadCount = testUser.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

        long startTime = System.currentTimeMillis();

        for(int i=0;i<threadCount;i++){
            final User user = testUser.get(i);
            executor.submit(() -> {
                try{
                    ChallengeParticipantResponseDto result = challengeParticipantService.joinChallenge(savedChallenge.getId(),user);
                    successCount.incrementAndGet();
                    System.out.println("User " + user.getId()+ " (" +user.getNickname() + ") 참여 성공");
                } catch(ApiException e){
                    failureCount.incrementAndGet();
                    errorMessages.add("User " +user.getId() + " : " + e.getErrorCode().name());
                    if(failureCount.get() <= 10) { //10개만 출력
                        System.out.println("User " +user.getId() + " 참여 실패 : "+ e.getErrorCode().name());
                    }
                } catch(Exception e){
                    failureCount.incrementAndGet();
                    errorMessages.add("User " +user.getId() + " : " + e.getMessage());
                    if(failureCount.get() <= 10) { //10개만 출력
                        System.out.println("User " +user.getId() + " 참여 실패 : "+  e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();

        long endTime = System.currentTimeMillis();

        // Then : 결과 확인
        Challenge result = challengeRepository.findById(savedChallenge.getId()).get();
        long actualDbCount = challengeParticipantRepository.countByChallengeId(savedChallenge.getId());

        System.out.println("\n=== 동시성 테스트 결과 ===");
        System.out.println("실행 시간 : " + (endTime - startTime) + "ms");
        System.out.println("성공한 요청: " + successCount + "개");
        System.out.println("실패한 요청: " + failureCount + "개");
        System.out.println("Entity의 참여자 수: " + result.getCurrentParticipants() + "명");
        System.out.println("실제 DB 레코드: " + actualDbCount + "건");

        // 🚨 동시성 문제 발생 확인
        if (result.getCurrentParticipants() > challenge.getMaxParticipants()) {
            System.out.println("\n 동시성 문제 발생!");
            System.out.println("   정원(" + challenge.getMaxParticipants() + "명)을 " +
                    (result.getCurrentParticipants() - challenge.getMaxParticipants()) + "명 초과했습니다!");
        }

        if (result.getCurrentParticipants() != actualDbCount) {
            System.out.println("\n 데이터 불일치 문제!");
            System.out.println("   Entity 참여자 수와 실제 DB 레코드 수가 다릅니다!");
        }

    }

    @Test
    @DisplayName("동시성 비교 테스트")
    public void concurrencyComparisonTest() throws InterruptedException {
        int threadCount = 5000;

        System.out.println("=== Synchronized 메서드 테스트 ===");
        long start1 = System.currentTimeMillis();
        testConcurrency(threadCount, challengeParticipantService::joinChallengeWithSync);
        System.out.println("Synchronized 소요 시간: " + (System.currentTimeMillis() - start1) + "ms\n");

        System.out.println("=== 챌린지별 락 테스트 ===");
        long start2 = System.currentTimeMillis();
        testConcurrency(threadCount, challengeParticipantService::joinChallengeWithChallengeSpecificLock);
        System.out.println("챌린지별 락 소요 시간: " + (System.currentTimeMillis() - start2) + "ms");
    }

    @Test
    @DisplayName("비관적락 & 낙관적락 테스트 & 분산락")
    public void concurrencyComparisonTest2() throws InterruptedException {
        int threadCount = 1000;

        System.out.println("=== 비관적 락 테스트 ===");
        long start1 = System.currentTimeMillis();
        testConcurrency(threadCount, challengeParticipantService::joinChallengeWithPessimisticLock);
        System.out.println("PessimisticLock 소요 시간 : " + (System.currentTimeMillis() - start1) + "ms");

        System.out.println("=== 낙관적 락 테스트 ===");
        long start2 = System.currentTimeMillis();
        testConcurrency(threadCount, challengeParticipantService::joinChallengeWithOptimisticLock);
        System.out.println("OptimisticLock 소요 시간 : " +  (System.currentTimeMillis() - start2) + "ms");

        System.out.println("=== 분산락 테스트 ===");
        long start3 = System.currentTimeMillis();
        testConcurrency(threadCount, challengeParticipantService::joinChallengeWithRedisLock);
        System.out.println("RedisRock 소요 시간 : " +  (System.currentTimeMillis() - start3) + "ms");
    }

    @Test
    @DisplayName("Redis 분산락 - 멀티 서버 시뮬레이션")
    void multiServerRedisLockSimulation() throws InterruptedException {
        int maxParticipants = 50;
        int totalUsers = 100; // 50명씩 2대 서버 시뮬레이션
        int serverCount = 2;

        Challenge challenge = createTestChallenge("멀티 서버 시뮬레이션 챌린지", maxParticipants);
        Challenge savedChallenge = challengeRepository.save(challenge);

        List<User> users = createTestUsers(totalUsers);

        // 서버별 스레드풀 생성 (멀티서버 시뮬레이션)
        ExecutorService[] servers = new ExecutorService[serverCount];
        for (int i = 0; i < serverCount; i++) {
            servers[i] = Executors.newFixedThreadPool(totalUsers / serverCount);
        }

        CountDownLatch latch = new CountDownLatch(totalUsers);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalUsers; i++) {
            final User user = users.get(i);
            // 서버 선택 (라운드 로빈)
            ExecutorService server = servers[i % serverCount];

            server.submit(() -> {
                try {
                    challengeParticipantService.joinChallengeWithRedisLock(savedChallenge.getId(), user.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        for (ExecutorService server : servers) {
            server.shutdown();
        }

        long endTime = System.currentTimeMillis();
        long actualDbCount = challengeParticipantRepository.countByChallengeId(savedChallenge.getId());

        System.out.println("\n=== 멀티 서버 Redis 분산락 테스트 결과 ===");
        System.out.println("실행 시간 : " + (endTime - startTime) + "ms");
        System.out.println("성공: " + successCount.get() + ", 실패: " + failureCount.get());
        System.out.println("DB 레코드: " + actualDbCount + "건");
        System.out.println("Entity 참여자 수: " + challengeRepository.findById(savedChallenge.getId()).get().getCurrentParticipants());
    }

    // ================= 헬퍼 메서드 =================
    private Challenge createTestChallenge(String title, int maxParticipants) {
        return Challenge.builder()
                .title(title)
                .description("동시성 테스트용 챌린지")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .targetCount(30L)
                .maxParticipants(maxParticipants)
                .active(true)
                .build();
    }

    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String email = "test" + UUID.randomUUID() + "@example.com";
            String nickname = "TestUser" + i;
            User user = new User(email,nickname ,nickname, "password123");
            users.add(userRepository.save(user));
        }
        return users;
    }


    // 멀티스레드 동시 실행용 헬퍼
    private void testConcurrency(int threadCount, BiConsumer<Long, Long> joinFunction) throws InterruptedException {
        Challenge challenge = createTestChallenge("동시성 테스트 챌린지", 200);
        Challenge savedChallenge = challengeRepository.save(challenge);
        List<User> userIds = createTestUsers(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = userIds.get(i).getId();
            executor.submit(() -> {
                try {
                    joinFunction.accept(savedChallenge.getId(), userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 결과 출력
        long dbCount = challengeParticipantRepository.countByChallengeId(savedChallenge.getId());
        System.out.println("성공: " + successCount.get() + ", 실패: " + failureCount.get());
        System.out.println("DB 레코드: " + dbCount + "건\n");
    }
}
