package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository {

    @Query("""
            SELECT n FROM NoteEntity n
            WHERE n.tenantId = :tenantId AND n.ticketId = :ticketId AND n.isDeleted = false
            """)
    List<NoteEntity> findActiveByTicket(UUID tenantId, UUID ticketId);
}
