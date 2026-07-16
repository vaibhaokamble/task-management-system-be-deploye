package com.organization.taskManagement.Model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeRegisterModel employeeId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskModel taskModel;
    private String message;
    private Instant createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
