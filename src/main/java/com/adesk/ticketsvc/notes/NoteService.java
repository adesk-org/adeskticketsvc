package com.adesk.ticketsvc.notes;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
import com.adesk.ticketsvc.notes.dto.NoteDto;
import com.adesk.ticketsvc.notes.dto.NoteList;
import com.adesk.ticketsvc.notes.dto.UpdateNoteRequest;
import com.adesk.ticketsvc.notes.mapper.NoteMapper;
import com.adesk.ticketsvc.notes.model.NoteEntity;
import com.adesk.ticketsvc.outbox.OutboxRepository;
import com.adesk.ticketsvc.outbox.model.OutboxEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;
    private final OutboxRepository outboxRepo;
    private final NoteMapper mapper;

    private final String ticketTopic = "ticket.events.v1";

    @Transactional(readOnly = true)
    public NoteList list(UUID tenantId, UUID ticketId, Integer limit, Integer offset) {
        int l = Math.min(1000, Math.max(1, limit));
        int o = Math.max(0, offset);
        int page = o / l;
        Pageable pageable = PageRequest.of(page, l, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<NoteEntity> pageData =
                noteRepo.findByTenantIdAndTicketIdAndIsDeletedFalse(tenantId, ticketId, pageable);
        List<NoteDto> items = mapper.toDtoList(pageData.getContent());

        return new NoteList(pageData.getTotalElements(), l, o, items);

    }

    @Transactional(readOnly = true)
    public NoteDto get(UUID tenantId, UUID ticketId, UUID noteId) {
        NoteEntity e = noteRepo
                .findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(tenantId, ticketId, noteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        return mapper.toDto(e);
    }

    @Transactional
    public NoteDto create(UUID tenantId, UUID ticketId, CreateNoteRequest req) {
        NoteEntity e =
                NoteEntity.builder().tenantId(tenantId).ticketId(ticketId).content(req.content())
                        .authorName(req.authorName()).isPrivate(false).isDeleted(false).build();
        e = noteRepo.saveAndFlush(e);
        recordOutbox(tenantId, e, "note.created");
        return mapper.toDto(e);
    }

    @Transactional
    public NoteDto update(UUID tenantId, UUID ticketId, UUID noteId, UpdateNoteRequest req) {
        NoteEntity e = noteRepo
                .findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(tenantId, ticketId, noteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        e.setContent(req.content());
        recordOutbox(tenantId, e, "note.updated");
        return mapper.toDto(e);
    }

    @Transactional
    public void delete(UUID tenantId, UUID ticketId, UUID noteId) {
        NoteEntity e = noteRepo
                .findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(tenantId, ticketId, noteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        e.setIsDeleted(true);
        recordOutbox(tenantId, e, "note.deleted");
    }

    private void recordOutbox(UUID tenantId, NoteEntity n, String eventType) {
        try {
            Map<String, Object> meta = new HashMap<>();
            meta.put("eventId", UUID.randomUUID().toString());
            meta.put("tenantId", tenantId.toString());
            meta.put("occurredAt", OffsetDateTime.now().toString());
            meta.put("domain", "ticket");
            meta.put("type", eventType);
            meta.put("version", 1);
            meta.put("aggregateId", n.getId().toString());
            meta.put("aggregateType", "Note");

            Map<String, Object> data = new HashMap<>();
            data.put("noteId", n.getId().toString());
            data.put("content", n.getContent());
            data.put("authorName", n.getAuthorName());
            data.put("createdAt", n.getCreatedAt());
            data.put("updatedAt", n.getUpdatedAt());

            Map<String, Object> payload = Map.of("meta", meta, "data", data);

            OutboxEntity o = OutboxEntity.builder().tenantId(tenantId).topic(ticketTopic)
                    .recordKey(tenantId + ":" + n.getId()).payload(payload)
                    .createdAt(OffsetDateTime.now()).build();
            outboxRepo.save(o);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record outbox event", e);
        }
    }
}
