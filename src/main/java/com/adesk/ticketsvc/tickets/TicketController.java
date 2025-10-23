package com.adesk.ticketsvc.tickets;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.tickets.dto.TicketCreate;
import com.adesk.ticketsvc.tickets.dto.TicketList;
import com.adesk.ticketsvc.tickets.dto.TicketUpdate;
import com.adesk.ticketsvc.tickets.model.TicketEntity;
import com.adesk.ticketsvc.tickets.model.TicketStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService svc;

    public TicketController(TicketService svc) {
        this.svc = svc;
    }

    @GetMapping
    public TicketList list(@RequestParam Integer limit, @RequestParam Integer offset,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String assignee) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        List<TicketEntity> items = svc.list(tenantId, limit, offset, status, assignee);
        int total = svc.count(tenantId, status, assignee);
        return new TicketList(total, limit, offset, items);
    }

    @PostMapping
    public ResponseEntity<TicketEntity> create(@Valid @RequestBody TicketCreate body) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        TicketEntity createdTicket = svc.create(tenantId, body);
        return ResponseEntity.created(URI.create("/tickets/" + createdTicket.getId()))
                .body(createdTicket);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketEntity> get(@PathVariable UUID ticketId) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        try {
            TicketEntity t = svc.get(tenantId, ticketId);
            return ResponseEntity.ok(t);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{ticketId}")
    public ResponseEntity<TicketEntity> patch(@PathVariable UUID ticketId,
            @RequestBody TicketUpdate body) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        try {
            return ResponseEntity.ok(svc.update(tenantId, ticketId, body));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
