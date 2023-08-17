package com.marketit.ordermanagement.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.ItemDto;
import com.marketit.ordermanagement.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FetchItemListAPITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestUtil testUtil;

    private Item item;

    @BeforeAll
    public void set() {
        item = testUtil.createItem();
    }

    @Test
    public void fetch_item_list_correctly() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/item/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    String json = response.getContentAsString();
                    CommonResponse<List<ItemDto>> commonResponse = objectMapper.readValue(json,
                            new TypeReference<CommonResponse<List<ItemDto>>>() {
                            });

                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commonResponse.getCode()).isEqualTo(ErrorCode.SUCCESS.getCode());

                    ItemDto itemDto = commonResponse.getData().get(0);

                    assertThat(itemDto.getName()).isEqualTo(item.getName());
                    assertThat(itemDto.getPrice()).isEqualTo(item.getPrice());
                });
    }
}
