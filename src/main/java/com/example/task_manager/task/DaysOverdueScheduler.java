package com.example.task_manager.task;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DaysOverdueScheduler {
    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateDaysOverdue() {
        LocalDate today = LocalDate.now();
        taskRepository.findAllByTaskComplete(false).forEach(task -> {
            int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(task.getDueDate(), today);
            task.setDaysOverdue(daysOverdue);
        });
    }
}
