package com.marketit.ordermanagement.controller;

import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.model.dto.ItemDto;
import com.marketit.ordermanagement.model.response.FetchItemListResponse;
import com.marketit.ordermanagement.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Operation(summary = "아이템 리스트 조회", description = "아이템 전체 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FetchItemListResponse.class)))
    @GetMapping(path = "/item/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<ItemDto>>> getItems() {
        List<ItemDto> items = itemService.getItems();
        return ResponseEntity.ok(CommonResponse.ok(items));
    }
}
