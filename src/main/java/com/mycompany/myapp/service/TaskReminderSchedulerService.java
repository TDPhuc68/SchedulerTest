package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@EnableScheduling
public class TaskReminderSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(TaskReminderSchedulerService.class);

    private final TaskRepository taskRepository;
    private final MailService mailService;
    private final SpringTemplateEngine templateEngine;
    private final SimpMessagingTemplate messagingTemplate; // dùng cho realtime

    public TaskReminderSchedulerService(
        TaskRepository taskRepository,
        MailService mailService,
        SpringTemplateEngine templateEngine,
        SimpMessagingTemplate messagingTemplate
    ) {
        this.taskRepository = taskRepository;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("✅ TaskReminderSchedulerService initialized - running every 30 minutes.");
    }

    // Chạy mỗi 30 phút
    @Scheduled(cron = "0 0/1 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void checkTaskReminders() {
        log.info("⏰ Checking tasks for reminders/overdue...");

        LocalDateTime now = LocalDateTime.now();

        // Lấy tất cả task chưa DONE
        List<Task> tasks = taskRepository.findAll(); // Bạn có thể viết query tối ưu hơn: chỉ task chưa DONE

        for (Task task : tasks) {
            // 1. Kiểm tra quá hạn
            if (task.getStatus() != Task.Status.DONE && now.isAfter(task.getEndTime())) {
                task.setStatus(Task.Status.OVERDUE);
                taskRepository.save(task);
                log.info("⚠️ Task '{}' marked as OVERDUE", task.getTitle());

                // Gửi email nếu task quan trọng
                if (Boolean.TRUE.equals(task.getImportant())) {
                    sendReminderEmail(task, "Task quá hạn!");
                }

                // Gửi realtime notification
                sendRealtimeNotification(task, "Task đã quá hạn!");
            }

            // 2. Kiểm tra nhắc nhở trước startTime
            if (task.getRemindBeforeHours() != null && task.getRemindBeforeHours() > 0) {
                LocalDateTime remindTime = task.getStartTime().minusHours(task.getRemindBeforeHours());
                if (now.isAfter(remindTime) && now.isBefore(task.getStartTime())) {
                    log.info("🔔 Sending reminder for task '{}'", task.getTitle());
                    sendReminderEmail(task, "Nhắc nhở Task sắp bắt đầu!");
                    sendRealtimeNotification(task, "Task sắp bắt đầu!");
                }
            }
        }
    }

    private void sendReminderEmail(Task task, String subjectPrefix) {
        try {
            Context context = new Context();
            context.setVariable("task", task);
            String htmlContent = templateEngine.process("mail/taskReminderEmail", context);

            String to = "dinhphuc0608@gmail.com"; // Hoặc lấy từ user liên quan nếu có quan hệ
            String subject = subjectPrefix + " - " + task.getTitle();

            mailService.sendEmail(to, subject, htmlContent, false, true);
            log.info("📧 Email reminder sent for task '{}'", task.getTitle());
        } catch (Exception e) {
            log.warn("❌ Failed to send email for task '{}'", task.getTitle(), e);
        }
    }

    private void sendRealtimeNotification(Task task, String message) {
        try {
            // gửi tới user qua websocket
            messagingTemplate.convertAndSendToUser(
                "dinhphuc0608", // username của người nhận
                "/queue/task-reminder",
                message + " - " + task.getTitle()
            );
            log.info("💬 Realtime notification sent for task '{}'", task.getTitle());
        } catch (Exception e) {
            log.warn("❌ Failed to send realtime notification for task '{}'", task.getTitle(), e);
        }
    }
}
