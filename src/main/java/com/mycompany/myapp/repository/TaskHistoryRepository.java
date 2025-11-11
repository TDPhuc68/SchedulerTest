package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TaskHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByModifiedAtDesc(Long taskId);
}
