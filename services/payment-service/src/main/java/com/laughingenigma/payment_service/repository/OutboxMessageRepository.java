package com.laughingenigma.payment_service.repository;

import com.laughingenigma.payment_service.entity.OutboxMessage;
import com.laughingenigma.payment_service.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxMessageRepository
        extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );
}
