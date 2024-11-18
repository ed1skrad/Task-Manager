package com.tech.task.repository.specification;

import com.tech.task.model.Task;
import com.tech.task.model.state.Priority;
import com.tech.task.model.state.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> filterTasks(Status status, Priority priority, Long creatorId, Long executorId) {
        return (Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (creatorId != null) {
                predicates.add(cb.equal(root.get("creator").get("id"), creatorId));
            }

            if (executorId != null) {
                predicates.add(cb.isMember(executorId, root.get("executors")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
