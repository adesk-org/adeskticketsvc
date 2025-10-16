package com.adesk.ticketsvc.outbox;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    @Query("""
            SELECT o FROM OutboxEntity o
            WHERE o.publishedAt IS NULL
            ORDER BY o.id ASC
            """)
    List<OutboxEntity> findUnpublished(Pageable pageable);

    @Modifying
    @Query("UPDATE OutboxEntity o SET o.publishedAt = CURRENT_TIMESTAMP where o.id = :id")
    void markPublished(@Param("id") Long id);
}
