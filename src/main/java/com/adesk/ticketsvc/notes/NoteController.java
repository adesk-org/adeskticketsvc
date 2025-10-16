package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
import com.adesk.ticketsvc.notes.dto.UpdateNoteRequest;
import com.adesk.ticketsvc.notes.model.NoteEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tickets/{ticketId}/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService service;

    @GetMapping
    public List<NoteEntity> listNotes(@PathVariable UUID ticketId) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        return service.list(tenantId, ticketId);
    }

    @GetMapping("/{noteId}")
    public NoteEntity getNote(@PathVariable UUID ticketId, @PathVariable UUID noteId) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        return service.get(tenantId, ticketId, noteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteEntity createNote(@PathVariable UUID ticketId,
            @Valid @RequestBody CreateNoteRequest body) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        return service.create(tenantId, ticketId, body);
    }

    @PatchMapping("/{noteId}")
    public NoteEntity updateNote(@PathVariable UUID ticketId, @PathVariable UUID noteId,
            @Valid @RequestBody UpdateNoteRequest content) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        return service.update(tenantId, ticketId, noteId, content);
    }

    @DeleteMapping("/{noteId}")
    public void deleteNote(@PathVariable UUID ticketId, @PathVariable UUID noteId) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        service.delete(tenantId, ticketId, noteId);
    }
}
