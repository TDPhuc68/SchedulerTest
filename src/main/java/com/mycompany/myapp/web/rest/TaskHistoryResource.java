package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.domain.TaskHistory;
import com.mycompany.myapp.repository.TaskHistoryRepository;
import com.mycompany.myapp.service.TaskHistoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task-history")
public class TaskHistoryResource {

    private final TaskHistoryService taskHistoryService;

    public TaskHistoryResource(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("/all")
    public List<TaskHistory> getAllTaskHistory() {
        return taskHistoryService.getAllHistory();
    }

    @GetMapping("/{taskId}")
    public List<TaskHistory> getTaskHistory(@PathVariable Long taskId) {
        return taskHistoryService.getHistory(taskId);
    }
}
