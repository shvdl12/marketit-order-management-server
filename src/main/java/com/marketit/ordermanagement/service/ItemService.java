package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.ItemDto;
import com.marketit.ordermanagement.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_ID_NOT_FOUND, itemId));
    }

    public List<ItemDto> getItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream().map(item -> modelMapper
                .map(item, ItemDto.class)).collect(Collectors.toList());
    }
}
