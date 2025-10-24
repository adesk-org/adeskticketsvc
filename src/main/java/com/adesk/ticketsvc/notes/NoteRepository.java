package com.adesk.ticketsvc.notes;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adesk.ticketsvc.notes.model.NoteEntity;

public interface NoteRepository extends JpaRepository<NoteEntity, UUID> {

    Page<NoteEntity> findByTenantIdAndTicketIdAndIsDeletedFalse(UUID tenantId, UUID ticketId,
            Pageable pageable);

    Optional<NoteEntity> findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(UUID noteId, UUID tenantId,
            UUID ticketId);
}
