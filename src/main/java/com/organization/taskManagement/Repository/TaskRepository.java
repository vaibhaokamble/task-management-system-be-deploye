package com.organization.taskManagement.Repository;

import com.organization.taskManagement.Model.TaskModel;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long> {

    List<TaskModel> findByProjectId(Long projectId);

    List<TaskModel> findByProjectIdIn(List<Long> projectIds);


}
