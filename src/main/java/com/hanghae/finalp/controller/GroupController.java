package com.hanghae.finalp.controller;

import com.hanghae.finalp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
}
