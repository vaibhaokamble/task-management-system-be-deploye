package com.organization.taskManagement.Repository;

import com.organization.taskManagement.Model.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {
    List<CommentModel> findByTaskModelIdOrderByCreatedAtAsc(Long taskId);
}
