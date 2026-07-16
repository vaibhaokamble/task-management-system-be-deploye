package com.organization.taskManagement.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.organization.taskManagement.Enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tasks")
@Setter
@Getter
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String teamId;

    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private EmployeeRegisterModel assignedTo;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectModel project;

    private Instant createdAt;
    private Instant updatedAt;
    @JsonManagedReference
    @OneToMany(mappedBy = "taskModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentModel> comments= new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() { 
        updatedAt = Instant.now();
    }

}
