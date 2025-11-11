package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Task;
import com.mycompany.myapp.service.dto.TaskDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO toDto(Task entity) {
        if (entity == null) return null;

        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setPriority(entity.getPriority());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setImportant(entity.getImportant());
        dto.setRemindBeforeHours(entity.getRemindBeforeHours());
        dto.setCron(entity.getCron());
        dto.setIntervalMinutes(entity.getIntervalMinutes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public Task toEntity(TaskDTO dto) {
        if (dto == null) return null;

        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setPriority(dto.getPriority());
        entity.setStatus(dto.getStatus() != null ? Task.Status.valueOf(dto.getStatus()) : null);
        entity.setImportant(dto.getImportant());
        entity.setRemindBeforeHours(dto.getRemindBeforeHours());
        entity.setCron(dto.getCron());
        entity.setIntervalMinutes(dto.getIntervalMinutes());
        return entity;
    }
}
