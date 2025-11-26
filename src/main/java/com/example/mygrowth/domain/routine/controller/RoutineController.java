package com.example.mygrowth.domain.routine.controller;
import com.example.mygrowth.domain.routine.dto.*;
import com.example.mygrowth.domain.routine.service.RoutineService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Routine", description = "Routine 관련 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
public class RoutineController {
    private final RoutineService routineService;

    /**
     * 루틴 생성 API
     * @param requestDto 루틴 생성 dto
     * @param customUserDetails 로그인 유저
     * @return 생성된 루틴 정보 dto
     */
    @PostMapping
    @Operation(summary="루틴 생성", description = "루틴을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "루틴 생성 성공",
            content = @Content(schema = @Schema(implementation = RoutineCreateResponseDto.class))
    )
    public ResponseEntity<RoutineCreateResponseDto> createRoutine(
            @Valid @RequestBody @Schema(implementation = RoutineRequestDto.class) RoutineRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        RoutineCreateResponseDto responseDto = routineService.creatRoutine(requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 루틴 id로 루틴 조회 API
     * @param id 루틴 id
     * @param date 날짜
     * @param customUserDetails 로그인 유저
     * @return 루틴 정보 dto
     */
    @GetMapping("/{id}")
    @Operation(summary="루틴 조회 id & date", description = "루틴 id와 date 포함하여 조회 - 루틴 수정 진입 시")
    @ApiResponse(responseCode = "200", description = "루틴 조회 성공",
            content = @Content(schema = @Schema(implementation = RoutineFindOneResponseDto.class))
    )
    public ResponseEntity<RoutineFindOneResponseDto> findRoutineById(
            @PathVariable Long id,
            @RequestParam("date") @Parameter(description = "조회할 날짜") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        RoutineFindOneResponseDto responseDto = routineService.findRoutineById(id, date, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 요일별로 루틴 리스트를 조회하기 위한 API
     * @param page 페이지 번호
     * @param customUserDetails 로그인한 유저
     * @param date 날짜
     * @return 루틴 정보 dto
     */
    @GetMapping("/by-date")
    @Operation(summary="루틴 리스트 조회 date", description = "date로 루틴 리스트 조회")
    @ApiResponse(responseCode = "200", description = "루틴 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = RoutineFindOneResponseDto.class))
    )
    public ResponseEntity<Page<RoutineFindResponseDto>> findRoutineByDate(
            @RequestParam(value = "page", defaultValue = "1")  @Parameter(description = "페이지 번호") int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("date") @Parameter(description = "조회할 날짜") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        Page<RoutineFindResponseDto> responseDto = routineService.findRoutineByDate(page, date, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 루틴 전체 조회 API
     * @param page 페이지 번호
     * @param customUserDetails 로그인 유저
     * @return 루틴 정보 dto
     */
    @GetMapping
    @Operation(summary="루틴 전체 조회", description = "로그인한 유저의 루틴 전체 조회")
    @ApiResponse(responseCode = "200", description = "루틴 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = RoutineFindOneResponseDto.class))
    )
    public ResponseEntity<List<RoutineFindResponseDto>> findRoutine(
            @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호") int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<RoutineFindResponseDto> responseDto = routineService.findRoutine(page, customUserDetails.getUser());
        if(responseDto == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 루틴 삭제 API
     * @param id 루틴 id
     * @param customUserDetails 로그인한 유저
     * @return void
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "루틴 삭제", description = "id로 전달받은 루틴 삭제")
    @ApiResponse(responseCode = "200", description = "루틴 삭제 성공")
    public ResponseEntity<Void> deleteRoutine(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        routineService.deleteRoutine(id, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 루틴 수정 API
     * @param id 루틴 id
     * @param requestDto 수정할 dto
     * @param deleteType 삭제 타입
     * @param selectedDate 선택된 날짜
     * @param customUserDetails 로그인한 유저
     * @return void
     */
    @PatchMapping("/{id}")
    @Operation(summary = "루틴 수정", description = "id로 전달받은 루틴 삭제 타입에 따라 수정")
    @ApiResponse(responseCode = "200", description = "루틴 수정 성공")
    public ResponseEntity<Void> updateRoutine(
            @PathVariable Long id,
            @Valid @RequestBody @Schema(implementation = RoutineRequestDto.class) RoutineRequestDto requestDto,
            @RequestParam("deleteType") @Parameter(description = "삭제 타입",example = "ALL_DAY", schema = @Schema(allowableValues ={"ALL_DAY","AFTER_DAY","ONLY_DAY"})) String deleteType,
            @RequestParam("selectedDate") @Parameter(description = "조회할 날짜") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        routineService.updateRoutine(id, requestDto, deleteType, selectedDate, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
