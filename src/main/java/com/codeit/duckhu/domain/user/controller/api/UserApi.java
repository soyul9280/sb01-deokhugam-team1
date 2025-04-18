package com.codeit.duckhu.domain.user.controller.api;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.exception.UserErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserApi {
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록성공",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "이메일 중복",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버오류",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class)))
    })
    ResponseEntity<UserDto> create(
            @Parameter(description = "회원가입 정보") @RequestBody UserRegisterRequest userRegisterRequest);


    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "로그인 실패(이메일 또는 비밀번호 불일치)",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버오류",
                content = @Content(schema = @Schema(implementation = UserErrorResponse.class)))
    })
    ResponseEntity<UserDto> create(
            @Parameter(description = "로그인 정보") @RequestBody UserLoginRequest userLoginRequest);
}
