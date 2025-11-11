package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.domain.TaskHistory;
import com.mycompany.myapp.repository.TaskHistoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    public TaskHistoryService(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    public void recordCreate(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getId());
        history.setAction("CREATE");
        history.setModifiedBy(getCurrentUser());
        taskHistoryRepository.save(history);
    }

    public void recordUpdate(Task oldTask, Task newTask) {
        if (!oldTask.getStatus().equals(newTask.getStatus())) {
            saveChanges(oldTask.getId(), "status", oldTask.getStatus().name(), newTask.getStatus().name());
        }
        if (!oldTask.getPriority().equals(newTask.getPriority())) {
            saveChanges(oldTask.getId(), "priority", oldTask.getPriority(), newTask.getPriority());
        }
    }

    public void recordDelete(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getId());
        history.setAction("DELETED");
        history.setModifiedBy(getCurrentUser());
        taskHistoryRepository.save(history);
    }

    public List<TaskHistory> getHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByModifiedAtDesc(taskId);
    }

    private void saveChanges(Long taskId, String field, String oldVal, String newVal) {
        TaskHistory h = new TaskHistory();
        h.setTaskId(taskId);
        h.setAction("UPDATED");
        h.setFieldChanged(field);
        h.setOldValue(oldVal);
        h.setNewValue(newVal);
        h.setModifiedBy(getCurrentUser());
        taskHistoryRepository.save(h);
    }

    public List<TaskHistory> getAllHistory() {
        return taskHistoryRepository.findAll();
    }

    private String getCurrentUser() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
