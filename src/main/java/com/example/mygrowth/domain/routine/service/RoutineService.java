package com.example.mygrowth.domain.routine.service;

import com.example.mygrowth.domain.routine.dto.*;
import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.entity.RoutineOverride;
import com.example.mygrowth.domain.routine.enums.RepeatType;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.routine.repository.RoutineOverrideRepository;
import com.example.mygrowth.domain.routine.repository.RoutineRepository;
import com.example.mygrowth.domain.routine.util.RoutineValidationUtils;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final RoutineLogRepository routineLogRepository;
    private final RoutineOverrideRepository routineOverrideRepository;

    @Transactional
    public RoutineCreateResponseDto creatRoutine(RoutineRequestDto requestDto, User loginUser) {
        // repeatType 선택 안했을 시 요일반복 default 처리
        RepeatType requestRepeatType = requestDto.getRepeatType() != null ? requestDto.getRepeatType() : RepeatType.DAY_OF_WEEK;

        List<DayOfWeek> daysOfWeek = requestDto.getDaysOfWeek();
        if(requestRepeatType == RepeatType.DAY_OF_WEEK && (daysOfWeek == null || daysOfWeek.isEmpty())){
            daysOfWeek = List.of(LocalDate.now().getDayOfWeek());
        }

        //RoutineValidationUtils.validateRoutine(requestDto); // 유효성 검사

        // 루틴 생성
        Routine newRoutine = new Routine(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestRepeatType,
                daysOfWeek,
                requestDto.getDayOfMonth(),
                requestDto.getDayOfYear(),
                requestDto.getGoalCount(),
                requestDto.isPublic(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                loginUser);

        Routine createRoutine = routineRepository.save(newRoutine);
        return new RoutineCreateResponseDto(
                createRoutine.getTitle(),
                createRoutine.getDescription(),
                createRoutine.getRepeatType(),
                createRoutine.getDaysOfWeek(),
                createRoutine.getDayOfMonth(),
                createRoutine.getDayOfYear(),
                createRoutine.getGoalCount(),
                createRoutine.isPublic(),
                createRoutine.getStartDate(),
                createRoutine.getEndDate()
        );
    }

    public RoutineFindOneResponseDto findRoutineById(Long id, LocalDate date, User loginUser) {
        // RoutineOverride 확인
        Optional<RoutineOverride> overrideRoutine = routineOverrideRepository.findByRoutineIdAndDate(id, date);

        if(overrideRoutine.isPresent()){
            return new RoutineFindOneResponseDto(
                    overrideRoutine.get().getId(),
                    overrideRoutine.get().getTitle(),
                    overrideRoutine.get().getDescription(),
                    overrideRoutine.get().getRepeatType(),
                    overrideRoutine.get().getDaysOfWeek(),
                    overrideRoutine.get().getDayOfMonth(),
                    overrideRoutine.get().getDayOfYear(),
                    overrideRoutine.get().getGoalCount(),
                    overrideRoutine.get().isPublic(),
                    overrideRoutine.get().getStartDate(),
                    overrideRoutine.get().getEndDate()
            );

        }
        Routine findRoutine = routineRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        return new RoutineFindOneResponseDto(
                findRoutine.getId(),
                findRoutine.getTitle(),
                findRoutine.getDescription(),
                findRoutine.getRepeatType(),
                findRoutine.getDaysOfWeek(),
                findRoutine.getDayOfMonth(),
                findRoutine.getDayOfYear(),
                findRoutine.getGoalCount(),
                findRoutine.isPublic(),
                findRoutine.getStartDate(),
                findRoutine.getEndDate()
        );
    }

    public List<RoutineFindResponseDto> findRoutine(int page, User user) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.asc("id")));

        Page<Routine> routinePage = routineRepository.findByUserId(pageable, user.getId());

        LocalDate today = LocalDate.now();

        return routinePage.stream()
                .map(routine -> {
                    // 해당 루틴의 오늘 날짜 로그 조회
                    Optional<RoutineLog> todayLog = routineLogRepository.findByRoutineIdAndDate(routine.getId(), today);

                    // isSuccess 값 설정
                    boolean isSuccess = todayLog.map(RoutineLog::isSuccess).orElse(false);

                    // DTO 반환
                    return new RoutineFindResponseDto(
                            routine.getId(),
                            isSuccess,
                            routine.getTitle()
                    );
                })
                .collect(Collectors.toList());

    }

    public Page<RoutineFindResponseDto> findRoutineByDate(int page, LocalDate date, User loginUser) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfMonth = date.getDayOfMonth();
        String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));

        // 유저 루틴 조회
        List<Routine> routineList = routineRepository.findByUserId(loginUser.getId());

        // Override 우선 필터링 및 DTO 생성
        List<RoutineFindResponseDto> filteredList = routineList.stream()
                // 시작일과 종료일 체크
                .filter(r -> !date.isBefore(r.getStartDate()) && (r.getEndDate() == null || !date.isAfter(r.getEndDate())))
                .map(r -> {
                    // 해당 날짜 Override 확인
                    Optional<RoutineOverride> overrideOpt = routineOverrideRepository.findByRoutineIdAndDate(r.getId(), date);

                    if (overrideOpt.isPresent()) {
                        RoutineOverride override = overrideOpt.get();
                        return new RoutineFindResponseDto(
                                r.getId(),
                                getIsSuccess(r.getId(), date),
                                override.getTitle()  // Override 제목 우선 적용
                        );
                    }

                    // Override 없으면 기본 루틴 기준
                    boolean matches = switch (r.getRepeatType()) {
                        case DAY_OF_WEEK -> r.getDaysOfWeek() != null && r.getDaysOfWeek().contains(dayOfWeek);
                        case MONTHLY -> r.getDayOfMonth() == dayOfMonth;
                        case YEARLY -> r.getDayOfYear() != null && r.getDayOfYear().equals(monthDay);
                        default -> false;
                    };

                    if (!matches) return null;

                    return new RoutineFindResponseDto(
                            r.getId(),
                            getIsSuccess(r.getId(), date),
                            r.getTitle()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        // 페이징 처리
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<RoutineFindResponseDto> paged = filteredList.stream()
                .skip(offset)
                .limit(pageSize)
                .toList();

        return new PageImpl<>(paged, PageRequest.of(page - 1, pageSize), filteredList.size());
    }


    // 로그 조회
    private boolean getIsSuccess(Long routineId, LocalDate date){
        return routineLogRepository.findByRoutineIdAndDate(routineId, date)
                .map(RoutineLog::isSuccess)
                .orElse(false);
    }

    @Transactional
    public void deleteRoutine(Long id, User loginUser) {
        Routine findRoutine = routineRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!findRoutine.getUser().getId().equals(loginUser.getId())){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        routineRepository.delete(findRoutine);
    }

    @Transactional
    public void updateRoutine(Long id, RoutineRequestDto requestDto, String deleteType, LocalDate selectedDate, User loginUser) {
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek();
        int dayOfMonth = selectedDate.getDayOfMonth();
        String monthDay = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd"));

        // repeatType 선택 안했을 시 요일반복 default 처리
        RepeatType requestRepeatType = requestDto.getRepeatType() != null ? requestDto.getRepeatType() : RepeatType.DAY_OF_WEEK;

        List<DayOfWeek> daysOfWeek = requestDto.getDaysOfWeek();
        if(requestRepeatType == RepeatType.DAY_OF_WEEK && (daysOfWeek == null || daysOfWeek.isEmpty())){
            daysOfWeek = List.of(LocalDate.now().getDayOfWeek());
        }

        RoutineValidationUtils.validateRoutine(requestDto); // 유효성 검사

        Routine findRoutine = routineRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!findRoutine.getUser().getId().equals(loginUser.getId())){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        if (requestRepeatType != RepeatType.DAY_OF_WEEK) {
            daysOfWeek = List.of(); // 빈 리스트로 초기화
        }

        switch (deleteType) {
            case "ALL_DAY":
                findRoutine.update(
                        requestDto.getTitle(),
                        requestDto.getDescription(),
                        requestRepeatType,
                        daysOfWeek,
                        requestDto.getDayOfMonth(),
                        requestDto.getDayOfYear(),
                        requestDto.getGoalCount(),
                        requestDto.isPublic(),
                        requestDto.getStartDate(),
                        requestDto.getEndDate()
                );
                break;
            case "AFTER_DAY":
                findRoutine.updateEndDate(selectedDate);

                // 루틴 생성
                Routine newRoutine = new Routine(
                        requestDto.getTitle(),
                        requestDto.getDescription(),
                        requestRepeatType,
                        daysOfWeek,
                        requestDto.getDayOfMonth(),
                        requestDto.getDayOfYear(),
                        requestDto.getGoalCount(),
                        requestDto.isPublic(),
                        selectedDate.plusDays(1),
                        requestDto.getEndDate(),
                        loginUser
                );

                routineRepository.save(newRoutine);
                break;
            case "ONLY_DAY":
                // 예외 처리 루틴 만들기
                RoutineOverride override = new RoutineOverride(
                    selectedDate,
                        requestDto.getTitle(),
                        requestDto.getDescription(),
                        requestDto.getRepeatType(),
                        requestDto.getDaysOfWeek(),
                        requestDto.getDayOfMonth(),
                        requestDto.getDayOfYear(),
                        requestDto.getGoalCount(),
                        requestDto.isPublic(),
                        requestDto.getStartDate(),
                        requestDto.getEndDate(),
                        findRoutine
                );
                routineOverrideRepository.save(override);
                break;
            default:
                throw new ApiException(ErrorCode.INVALID_DELETE_TYPE);
        }

    }


}
