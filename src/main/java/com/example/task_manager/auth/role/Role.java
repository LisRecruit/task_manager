package com.example.task_manager.auth.role;


import com.example.task_manager.auth.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "role_seq", sequenceName = "seq_roles_id", allocationSize = 1)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<User> users = new ArrayList<>();
}
