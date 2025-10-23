package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
import com.adesk.ticketsvc.notes.dto.UpdateNoteRequest;
import com.adesk.ticketsvc.notes.model.NoteEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;

    public List<NoteEntity> list(UUID tenantId, UUID ticketId, Integer limit, Integer offset) {
        int l = Math.min(1000, Math.max(1, limit));
        int o = Math.max(0, offset);
        int page = o / l;
        AbstractPageRequest pageable =
                PageRequest.of(page, l, Sort.by(Sort.Direction.DESC, "createdAt"));

        return noteRepo.pageByFilters(tenantId, ticketId, pageable).getContent();
    }

    public NoteEntity get(UUID tenantId, UUID ticketId, UUID noteId) {
        NoteEntity n = noteRepo.getForTenantTicket(tenantId, ticketId, noteId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        if (n.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        return n;
    }

    public NoteEntity create(UUID tenantId, UUID ticketId, CreateNoteRequest req) {
        NoteEntity note =
                NoteEntity.builder().tenantId(tenantId).ticketId(ticketId).content(req.content())
                        .author(req.authorName()).isPrivate(false).isDeleted(false).build();
        noteRepo.save(note);
        return note;
    }

    public NoteEntity update(UUID tenantId, UUID ticketId, UUID noteId, UpdateNoteRequest req) {
        NoteEntity n = get(tenantId, ticketId, noteId);
        n.setContent(req.getContent());
        return n;
    }

    public void delete(UUID tenantId, UUID ticketId, UUID noteId) {
        NoteEntity n = get(tenantId, ticketId, noteId);
        n.setIsDeleted(true);
    }

    public int count(UUID tenantId, UUID ticketId) {
        return noteRepo.countByFilters(tenantId, ticketId);
    }
}
