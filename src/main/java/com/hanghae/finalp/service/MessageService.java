package com.hanghae.finalp.service;

import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Slice<MessageDto.SendRes> getPreviousMessage(Long chatroomId, Pageable pageable) {
        return messageRepository.findByChatroomId(chatroomId, pageable).map(MessageDto.SendRes::new);
    }
}
