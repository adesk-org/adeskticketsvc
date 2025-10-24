package com.adesk.ticketsvc.notes;

import java.time.OffsetDateTime;
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
import com.adesk.ticketsvc.tickets.model.TicketEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;
    private final OutboxRepository outboxRepo;
    private final NoteMapper mapper;

    private final String ticketTopic = "note.events.v1";

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
        return mapper.toDto(e);
    }

    @Transactional
    public NoteDto update(UUID tenantId, UUID ticketId, UUID noteId, UpdateNoteRequest req) {
        NoteEntity e = noteRepo
                .findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(tenantId, ticketId, noteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        e.setContent(req.content());
        return mapper.toDto(e);
    }

    @Transactional
    public void delete(UUID tenantId, UUID ticketId, UUID noteId) {
        NoteEntity e = noteRepo
                .findByTenantIdAndTicketIdAndIdAndIsDeletedFalse(tenantId, ticketId, noteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
        e.setIsDeleted(true);
    }

    private void recordOutbox(UUID tenantId, TicketEntity t, String eventName) {
        try {
            Map<String, Object> payload = Map.of("meta", Map.of("eventId",
                    UUID.randomUUID().toString(), "occurredAt", OffsetDateTime.now().toString(),
                    "tenantId", tenantId.toString(), "domain", "ticket", "name", eventName,
                    "version", 1, "aggregateId", t.getId().toString(), "aggregateType", "Ticket"),
                    "data",
                    Map.of("ticketId", t.getId().toString(), "title", t.getTitle(), "status",
                            t.getStatus().toString(), "description", t.getDescription(), "assignee",
                            t.getAssignee(), "createdAt", t.getCreatedAt(), "updatedAt",
                            t.getUpdatedAt()));

            OutboxEntity entity = OutboxEntity.builder().tenantId(tenantId).topic(ticketTopic)
                    .recordKey(tenantId + ":" + t.getId()).payload(payload)
                    .createdAt(OffsetDateTime.now()).build();
            outboxRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record outbox event", e);
        }
    }
}
