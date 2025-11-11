package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.repository.TaskRepository;
import com.mycompany.myapp.service.dto.TaskDTO;
import com.mycompany.myapp.service.mapper.TaskMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repo;
    private final TaskMapper mapper;
    private final TaskHistoryService historyService;

    public TaskService(TaskRepository repo, TaskMapper mapper, TaskHistoryService historyService) {
        this.repo = repo;
        this.mapper = mapper;
        this.historyService = historyService;
    }

    //    public List<Task> getTasksToday() {
    //        LocalDate today = LocalDate.now();
    //        return repo.findAllByStartTime(today);
    //    }

    /**
     * Lấy toàn bộ danh sách task
     */
    public List<TaskDTO> findAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Lấy task theo ID
     */
    public TaskDTO findById(Long id) {
        return repo.findById(id).map(mapper::toDto).orElse(null);
    }

    /**
     * Lưu hoặc cập nhật task, đồng thời ghi lịch sử (CREATE / UPDATE)
     */
    public TaskDTO save(TaskDTO dto) {
        Task task = mapper.toEntity(dto);
        boolean isNew = (task.getId() == null);

        Task oldTask = null;
        if (!isNew) {
            oldTask = repo.findById(task.getId()).orElse(null);
        }

        Task savedTask = repo.save(task);

        // Ghi lịch sử
        if (isNew) {
            historyService.recordCreate(savedTask);
        } else if (oldTask != null) {
            historyService.recordUpdate(oldTask, savedTask);
        }

        return mapper.toDto(savedTask);
    }

    /**
     * Xóa task, đồng thời ghi lịch sử (DELETE)
     */
    public void delete(Long id) {
        Task task = repo.findById(id).orElse(null);
        if (task != null) {
            repo.delete(task);
            historyService.recordDelete(task);
        }
    }
}
