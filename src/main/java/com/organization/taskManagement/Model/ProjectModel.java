package com.organization.taskManagement.Model;

import com.organization.taskManagement.Enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "projects")
@Setter
@Getter
public class ProjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String projectName;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private EmployeeRegisterModel createdBy;

    @ManyToMany
    @JoinTable(
        name = "project_employees",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @Builder.Default
    private List<EmployeeRegisterModel> employees = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
             orphanRemoval = true
    )
    @Builder.Default
    private List<TaskModel> tasks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }


}
