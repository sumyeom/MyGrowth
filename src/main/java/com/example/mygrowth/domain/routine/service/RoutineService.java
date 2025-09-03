package com.example.mygrowth.domain.routine.service;

import com.example.mygrowth.domain.routine.dto.*;
import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.enums.RepeatType;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;

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

    public RoutineFindOneResponseDto findRoutineById(Long id, User loginUser) {
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

        return routinePage.stream()
                .map(routine -> new RoutineFindResponseDto(
                        routine.getId(),
                        routine.getTitle(),
                        routine.getRepeatType()
                ))
                .collect(Collectors.toList());
    }

    public Page<RoutineFindResponseDto> findRoutineByDate(int page, LocalDate date, User loginUser) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfMonth = date.getDayOfMonth();
        String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Routine> routineList = routineRepository.findByUserId(loginUser.getId());
        List<Routine> filteredList = routineList.stream()
                .filter(r -> !date.isBefore(r.getStartDate()) && !date.isAfter(r.getEndDate()))
                .filter(r-> {
                    switch(r.getRepeatType()){
                        case DAY_OF_WEEK:
                            return r.getDaysOfWeek() != null && r.getDaysOfWeek().contains(dayOfWeek);
                        case MONTHLY:
                            return r.getDayOfMonth() == dayOfMonth;
                        case YEARLY:
                            return r.getDayOfYear() != null && r.getDayOfYear().equals(monthDay);
                        default:
                            return false;
                    }
                })
                .toList();

        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<RoutineFindResponseDto> paged = filteredList.stream()
                .skip(offset)
                .limit(pageSize)
                .map(RoutineFindResponseDto::fromEntity)
                .toList();
        return new PageImpl<>(paged, PageRequest.of(page - 1, pageSize), filteredList.size());
    }

    @Transactional
    public void deleteRoutine(Long id, User user) {
        Routine findRoutine = routineRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!findRoutine.getUser().getId().equals(user.getId())){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        routineRepository.delete(findRoutine);
    }

    @Transactional
    public void updateRoutine(Long id, RoutineRequestDto requestDto, User loginUser) {
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
    }


}
