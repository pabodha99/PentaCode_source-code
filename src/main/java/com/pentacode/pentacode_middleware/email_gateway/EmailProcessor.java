package com.pentacode.pentacode_middleware.email_gateway;

import com.pentacode.pentacode_middleware.email_gateway.dto.SystemBlockingQueue;
import com.pentacode.pentacode_middleware.email_gateway.service.EmailService;
import org.apache.logging.log4j.LogManager;
import com.pentacode.pentacode_middleware.email_gateway.dto.SendEmailRequest;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Service
public class EmailProcessor {

    private static Logger audit = LogManager.getLogger("audit-log");

    BlockingQueue<SendEmailRequest> highPriorityQueue = new LinkedBlockingQueue<>(5);
    BlockingQueue<SendEmailRequest> mediumPriorityQueue = new LinkedBlockingQueue<>(5);
    BlockingQueue<SendEmailRequest> lowPriorityQueue = new LinkedBlockingQueue<>(5);

    SystemBlockingQueue queue1 = new SystemBlockingQueue(highPriorityQueue, "HighPriorityQueue");
    SystemBlockingQueue queue2 = new SystemBlockingQueue(mediumPriorityQueue, "MediumPriorityQueue");
    SystemBlockingQueue queue3 = new SystemBlockingQueue(lowPriorityQueue, "LowPriorityQueue");


    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    @Autowired
    private EmailService emailService;

    public EmailProcessor() {
        startEmailGateway();
        audit.info("email,gateway,start,at," + LocalDateTime.now() + ",success,ok");
    }

    public void sendEmail(SendEmailRequest sendEmailRequest) {
        switch (sendEmailRequest.getEmailPrority()) {
            case HIGH -> {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        highPriorityQueue.offer(sendEmailRequest, 0, TimeUnit.MICROSECONDS);
                    } catch (InterruptedException e) {
                        audit.info("email,gateway,high priority queue insertion,error,at," + LocalDateTime.now() + ","
                                + sendEmailRequest.getTo() + "," + e.getMessage());
                    }
                });
                break;
            }
            case MEDIUM -> {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        mediumPriorityQueue.offer(sendEmailRequest, 30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        audit.info("email,gateway,medium priority queue insertion,error,at," + LocalDateTime.now() + ","
                                + sendEmailRequest.getTo() + "," + e.getMessage());
                    }
                });
                break;
            }
            case LOW -> {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        lowPriorityQueue.offer(sendEmailRequest, 60, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        audit.info("email,gateway,low priority queue insertion,error,at," + LocalDateTime.now() + ","
                                + sendEmailRequest.getTo() + "," + e.getMessage());
                    }
                });
                break;
            }
        }
    }

    public void startEmailGateway() {
        executorService.submit(() -> processQueues(queue1));
        executorService.submit(() -> processQueues(queue2));
        executorService.submit(() -> processQueues(queue3));
    }

    public void processQueues(SystemBlockingQueue systemBlockingQueue) {
        SendEmailRequest sendEmailRequest = null;
        while (true) {
            try {
                sendEmailRequest = systemBlockingQueue.getBlockingQueue().take();

            } catch (InterruptedException e) {
                audit.info("email,processor,queue," + systemBlockingQueue.getName() + ",at," + LocalDateTime.now() + ",error," + e.getMessage());
            }
            emailService.sendMail(sendEmailRequest);
        }
    }

    public void stopEmailGateway() {
        executorService.shutdownNow();
    }

}