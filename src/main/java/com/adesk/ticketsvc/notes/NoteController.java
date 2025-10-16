package com.adesk.ticketsvc.notes;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteEntity createNote(@PathVariable UUID ticketId,
            @Valid @RequestBody CreateNoteRequest body) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        return service.create(tenantId, ticketId, body);
    }
}
