package com.pentacode.pentacode_middleware.email_gateway.dto;


import lombok.Data;

import java.util.concurrent.BlockingQueue;

@Data
public class SystemBlockingQueue {
    private final BlockingQueue<SendEmailRequest> blockingQueue;
    private final String name;

    public SystemBlockingQueue(BlockingQueue<SendEmailRequest> blockingQueue, String name) {
        this.blockingQueue = blockingQueue;
        this.name = name;
    }
}
