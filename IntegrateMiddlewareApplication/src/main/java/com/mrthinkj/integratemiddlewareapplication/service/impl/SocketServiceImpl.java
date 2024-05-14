package com.mrthinkj.integratemiddlewareapplication.service.impl;

import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocketServiceImpl implements SocketService {
    SimpMessageSendingOperations messagingTemplate;
    @Override
    public void sendToTopic(String topic, Object content) {
        messagingTemplate.convertAndSend(topic, content);
    }
}
