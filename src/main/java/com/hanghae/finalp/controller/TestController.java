package com.hanghae.finalp.controller;

import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;

    @GetMapping("/test")
    public String test() {
        return "test";
    }


    @PostConstruct
    public void createDummyMember() {
        Member dummy = Member.createMember("1", "testUser", null);
        memberRepository.save(dummy);
    }

}
