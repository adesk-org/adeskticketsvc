package com.adesk.ticketsvc.tickets;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    @Query("""
            SELECT t FROM TicketEntity t
            WHERE t.tenantId = :tenantId
                AND (COALESCE(:status, t.status) = t.status)
                AND (:assignee IS NULL OR t.assignee = :assignee)
                """)
    Page<TicketEntity> pageByFilters(@Param("tenantId") UUID tenantId,
            @Param("status") TicketStatus status, @Param("assignee") String assignee,
            Pageable pageable);


    @Query("""
            SELECT COUNT(t) from TicketEntity t
            WHERE t.tenantId = :tenantId
                AND (COALESCE(:status, t.status) = t.status)
                AND (:assignee IS NULL OR t.assignee = :assignee)
            """)
    int countByFilters(@Param("tenantId") UUID tenantId, @Param("status") TicketStatus status,
            @Param("assignee") String assignee);

    Optional<TicketEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
