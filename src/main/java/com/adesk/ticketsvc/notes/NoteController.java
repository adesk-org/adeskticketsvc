package com.adesk.ticketsvc.notes;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.notes.dto.CreateNoteRequest;
import com.adesk.ticketsvc.notes.dto.NoteDto;
import com.adesk.ticketsvc.notes.dto.NoteList;
import com.adesk.ticketsvc.notes.dto.UpdateNoteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tickets/{ticketId}/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService service;

    @GetMapping
    public NoteList listNotes(@RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID ticketId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset) {
        return service.list(tenantId, ticketId, limit, offset);
    }

    @GetMapping("/{noteId}")
    public NoteDto getNote(@RequestHeader("X-Tenant-Id") UUID tenantId, @PathVariable UUID ticketId,
            @PathVariable UUID noteId) {
        return service.get(tenantId, ticketId, noteId);
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(@RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID ticketId, @Valid @RequestBody CreateNoteRequest body) {
        var created = service.create(tenantId, ticketId, body);
        URI location = URI.create(String.format("/tickets/%s/notes/%s", ticketId, created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{noteId}")
    public NoteDto updateNote(@RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID ticketId, @PathVariable UUID noteId,
            @Valid @RequestBody UpdateNoteRequest content) {
        return service.update(tenantId, ticketId, noteId, content);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteNote(@RequestHeader("X-Tenant-Id") UUID tenantId, @PathVariable UUID ticketId,
            @PathVariable UUID noteId) {
        service.delete(tenantId, ticketId, noteId);
    }
}
