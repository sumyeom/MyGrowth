package com.example.mygrowth.domain.challenge.service;

import com.example.mygrowth.domain.challenge.dto.ChallengeListResponseDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeResponseDto;
import com.example.mygrowth.domain.challenge.entity.Challenge;
import com.example.mygrowth.domain.challenge.repository.ChallengeRepository;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지 생성 함수
     * @param requestDto 챌린지 생성 dto
     * @return 챌린지 정보 dto
     */
    @Transactional
    public ChallengeResponseDto createChallenge(ChallengeRequestDto requestDto) {
        // 챌린지 생성
        Challenge challenge = new Challenge(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getTargetCount(),
                requestDto.getMaxParticipants(),
                requestDto.isActive()
        );

        Challenge savedChallenge = challengeRepository.save(challenge);

        return new ChallengeResponseDto(
                savedChallenge.getTitle(),
                savedChallenge.getDescription(),
                savedChallenge.getStartDate(),
                savedChallenge.getEndDate(),
                savedChallenge.getTargetCount(),
                savedChallenge.getMaxParticipants(),
                savedChallenge.getCurrentParticipants(),
                savedChallenge.isActive()
        );
    }

    /**
     * 챌린지 단건 조회 함수
     *
     * @param id 챌린지 id
     * @return 챌린지 정보 dto
     */
    public ChallengeResponseDto findByIdChallenge(Long id) {
        // 챌린지 조회
        Challenge findChallenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        return new ChallengeResponseDto(
                findChallenge.getTitle(),
                findChallenge.getDescription(),
                findChallenge.getStartDate(),
                findChallenge.getEndDate(),
                findChallenge.getTargetCount(),
                findChallenge.getMaxParticipants(),
                findChallenge.getCurrentParticipants(),
                findChallenge.isActive()
        );
    }

    /**
     * 챌린지 전체 조회
     * @param page 페이지 번호
     * @return 챌린지 리스트
     */
    public Page<ChallengeListResponseDto> findAllChallenges(int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("CreatedAt").descending());

        Page<Challenge> challenges = challengeRepository.findAll(pageable);
        return challenges.map(c -> new ChallengeListResponseDto(
                c.getTitle(),
                c.getStartDate(),
                c.getEndDate(),
                c.isActive()
        ));
    }

    /**
     * 챌린지 수정 함수
     * @param id 챌린지 id
     * @param requestDto 챌린지 수정 정보 dto
     */
    @Transactional
    public void updateChallenge(Long id, ChallengeRequestDto requestDto) {
        // 챌린지 조회
        Challenge findChallenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 챌린지 업데이트
        findChallenge.updateChallenge(requestDto);

    }

    /**
     * 챌린지 삭제 함수
     * @param id 챌린지 id
     */
    public void deleteChallenge(Long id) {
        // 챌린지 조회
        Challenge findChallenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 챌린지 삭제
        challengeRepository.delete(findChallenge);
    }
}
