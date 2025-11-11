package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // List<Task> findAllByStartTime(LocalDate startTime);
    List<Task> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
