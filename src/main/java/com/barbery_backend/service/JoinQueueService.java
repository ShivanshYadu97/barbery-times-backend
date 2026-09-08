package com.barbery_backend.service;

import com.barbery_backend.dto.JoinQueueRequest;
import com.barbery_backend.dto.QueueResponse;
import com.barbery_backend.entity.CurrentQueue;

import java.util.List;

public interface JoinQueueService {

    CurrentQueue joinQueue(JoinQueueRequest request);

    void clearQueue();

    List<QueueResponse> getQueueByShopAndBarber(Long shopId, Long barberId);

    QueueResponse getQueueByShopAndCustomer(Long shopId, Long customerId);

    CurrentQueue startService(Long queueId);    //start button at barber

    void stopService(Long queueId);

}