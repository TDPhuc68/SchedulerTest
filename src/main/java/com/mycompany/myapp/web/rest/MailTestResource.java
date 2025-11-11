package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.DailyTaskMailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailTestResource {

    private final DailyTaskMailService dailyTaskMailService;

    public MailTestResource(DailyTaskMailService dailyTaskMailService) {
        this.dailyTaskMailService = dailyTaskMailService;
    }

    @GetMapping("/send-daily")
    public String testMail() {
        dailyTaskMailService.sendDailyTaskSummary();
        return "Email đã được gửi!";
    }
}
