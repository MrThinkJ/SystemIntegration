package com.mrthinkj.integratemiddlewareapplication.service;

public interface SocketService {
    void sendToTopic(String topic, Object content);
}
