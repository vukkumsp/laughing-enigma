package com.laughingenigma.saga_orchestrator.repository;

import com.laughingenigma.saga_orchestrator.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
    Optional<SagaInstance> findByCorrelationId(String correlationId);
}
