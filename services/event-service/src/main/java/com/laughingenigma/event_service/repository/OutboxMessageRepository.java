package com.laughingenigma.event_service.repository;

import com.laughingenigma.event_service.entity.OutboxMessage;
import com.laughingenigma.event_service.entity.OutboxStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxMessageRepository
        extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
    // "-2" translates to SKIP LOCKED in PostgreSQL/Oracle
    @Query("""
    SELECT o
    FROM OutboxMessage o
    WHERE o.status = :status
      AND (o.nextRetryAt IS NULL OR o.nextRetryAt <= :now)
    ORDER BY o.createdAt ASC
    """)
    List<OutboxMessage> findReadyMessages(
            @Param("status") OutboxStatus status,
            @Param("now") Instant now,
            Pageable pageable
    );
}
