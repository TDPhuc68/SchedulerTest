package com.mycompany.myapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskSchedulerService {

    private final DailyTaskMailService dailyTaskMailService;

    public TaskSchedulerService(DailyTaskMailService dailyTaskMailService) {
        this.dailyTaskMailService = dailyTaskMailService;
    }

    // Gửi mail lúc 8h sáng mỗi ngày
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyTaskEmailJob() {
        dailyTaskMailService.sendDailyTaskSummary();
    }
}
