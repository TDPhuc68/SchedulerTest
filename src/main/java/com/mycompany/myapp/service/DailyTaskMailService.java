package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@EnableScheduling
public class DailyTaskMailService {

    private static final Logger log = LoggerFactory.getLogger(DailyTaskMailService.class);

    private final MailService mailService;
    private final TaskRepository taskRepository;
    private final SpringTemplateEngine templateEngine;

    public DailyTaskMailService(MailService mailService, TaskRepository taskRepository, SpringTemplateEngine templateEngine) {
        this.mailService = mailService;
        this.taskRepository = taskRepository;
        this.templateEngine = templateEngine;
    }

    @PostConstruct
    public void init() {
        log.info("✅ DailyTaskMailService initialized - email scheduler active.");
    }

    // Lên lịch chạy 8h sáng mỗi ngày
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyTaskSummary() {
        log.info("📧 Starting daily task email job...");

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Task> tasks = taskRepository.findByStartTimeBetween(startOfDay, endOfDay);

        if (tasks.isEmpty()) {
            log.info("Không có công việc nào cho hôm nay.");
            return;
        }

        // Tạo nội dung email bằng Thymeleaf
        Context context = new Context();
        context.setVariable("date", today);
        context.setVariable("tasks", tasks);
        String htmlContent = templateEngine.process("mail/dailyTaskEmail", context);

        String to = "dinhphuc0608@gmail.com"; // 👉 Thay địa chỉ nhận mail thực tế
        String subject = "Danh sách công việc hôm nay - " + today;

        mailService.sendEmail(to, subject, htmlContent, false, true);
        log.info("✅ Email danh sách công việc đã được gửi tới {}", to);
    }
}
