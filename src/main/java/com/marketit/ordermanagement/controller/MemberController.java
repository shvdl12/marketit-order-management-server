package com.marketit.ordermanagement.controller;

import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.model.dto.MemberDto;
import com.marketit.ordermanagement.model.response.FetchUserResponse;
import com.marketit.ordermanagement.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "유저 조회", description = "테스트를 위한 유저 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FetchUserResponse.class)))
    @GetMapping(path = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<MemberDto>> getMember() {
        MemberDto memberDto = memberService.getMember();
        return ResponseEntity.ok(CommonResponse.ok(memberDto));
    }
}
