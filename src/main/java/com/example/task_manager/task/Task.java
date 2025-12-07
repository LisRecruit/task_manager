package com.example.task_manager.task;

import com.example.task_manager.auth.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_seq", sequenceName = "seq_tasks_id", allocationSize = 1)
    private Long id;

    @Column(name = "task_description", nullable = false)
    private String taskDescription;
    @Column(name = "responsible_person_note")
    private String responsiblePersonNote;
    @Column(name = "direct_manager_note")
        private String directManagerNote;
    @ManyToOne
    @JoinColumn(name = "responsible_user_id")
    @NotNull
    private User responsiblePerson;
//    @Column(name = "task_set_by", nullable = false)
    @ManyToOne
    @JoinColumn(name = "task_set_by_user_id")
    @NotNull
    private User taskSetBy;
    @Column (name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column (name = "completion_date")
    private LocalDate completionDate;
    @Column (name = "days_overdue")
    private Integer daysOverdue;
    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable(
            name = "sub_tasks",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_task_id")
    )
    private List<Task> subTasks = new ArrayList<>();
    @Column(name = "task_complete", nullable = false)
    private Boolean taskComplete;
    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Column(name = "repeatable", nullable = false)
    private Boolean repeatable;
    @Column (name = "repeatable_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepeatableType repeatableType;

    @Column(name = "period", nullable = false)
    private LocalDate period;




}
