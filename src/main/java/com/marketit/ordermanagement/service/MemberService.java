package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.MemberDto;
import com.marketit.ordermanagement.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, userId));
    }

    public MemberDto getMember() {
        Member member = memberRepository.findAll().get(0);
        return modelMapper.map(member, MemberDto.class);
    }
}
