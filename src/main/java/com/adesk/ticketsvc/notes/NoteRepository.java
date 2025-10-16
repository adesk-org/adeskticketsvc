package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.adesk.ticketsvc.notes.model.NoteEntity;

public interface NoteRepository extends JpaRepository<NoteEntity, UUID> {

    @Query("""
            SELECT n FROM NoteEntity n
            WHERE n.tenantId = :tenantId
                AND n.ticketId = :ticketId
                AND n.isDeleted = false
            """)
    List<NoteEntity> findByTenantTicket(UUID tenantId, UUID ticketId);

    @Query("""
            SELECT n FROM NoteEntity n
            WHERE n.id = :noteId
                AND n.tenantId = :tenantId
                AND n.ticketId = :ticketId
                AND n.isDeleted = false
            """)
    Optional<NoteEntity> getForTenantTicket(UUID tenantId, UUID ticketId, UUID noteId);
}
