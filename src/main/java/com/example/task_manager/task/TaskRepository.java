package com.example.task_manager.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository <Task, Long>{
    List<Task> findAllByResponsiblePersonId(Long responsiblePersonId);
    List<Task> findAllByResponsiblePersonIdIn(List<Long> userIds);

}
