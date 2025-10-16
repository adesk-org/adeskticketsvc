package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;

    public List<NoteEntity> list(UUID tenantId, UUID ticketId) {
        return noteRepo.findActiveByTicket(tenantId, ticketId);
    }

    public NoteEntity create(UUID tenantId, UUID ticketId, CreateNoteRequest req) {
        NoteEntity note =
                NoteEntity.builder().tenantId(tenantId).ticketId(ticketId).content(req.content())
                        .author(req.authorName()).isPrivate(false).isDeleted(false).build();
        noteRepo.save(note);
        return note;
    }
}
