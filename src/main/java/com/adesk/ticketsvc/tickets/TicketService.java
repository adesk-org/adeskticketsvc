package com.adesk.ticketsvc.tickets;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.adesk.ticketsvc.outbox.OutboxRepository;
import com.adesk.ticketsvc.outbox.model.OutboxEntity;
import com.adesk.ticketsvc.tickets.dto.TicketCreate;
import com.adesk.ticketsvc.tickets.dto.TicketUpdate;
import com.adesk.ticketsvc.tickets.model.TicketEntity;
import com.adesk.ticketsvc.tickets.model.TicketStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepo;
    private final OutboxRepository outboxRepo;

    private final String ticketTopic = "ticket.events.v1";

    public TicketEntity get(UUID tenantId, UUID ticketId) {
        return ticketRepo.findByIdAndTenantId(ticketId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }

    public TicketEntity create(UUID tenantId, TicketCreate req) {
        TicketEntity t = new TicketEntity();
        t.setTenantId(tenantId);
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setAssignee(req.getAssignee());
        t.setStatus(TicketStatus.OPEN);
        t = ticketRepo.save(t);
        recordOutbox(tenantId, t, "ticket.created");
        return t;
    }

    public List<TicketEntity> list(UUID tenantId, Integer limit, Integer offset,
            TicketStatus status, String assignee) {
        int l = Math.min(1000, Math.max(1, limit));
        int o = Math.max(0, offset);
        int page = o / l;
        AbstractPageRequest pageable =
                PageRequest.of(page, l, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ticketRepo.pageByFilters(tenantId, status, assignee, pageable).getContent();
    }

    public int count(UUID tenantId, TicketStatus status, String assignee) {
        return ticketRepo.countByFilters(tenantId, status, assignee);
    }

    public TicketEntity update(UUID tenantId, UUID ticketId, TicketUpdate req) {
        TicketEntity t = get(tenantId, ticketId);
        if (req.getStatus() != null) {
            t.setStatus(req.getStatus());
        }

        // Apply assignee only if provided; null means unassign
        if (req.isAssigneePresent()) {
            t.setAssignee(req.getAssignee());
        }

        ticketRepo.save(t);
        recordOutbox(tenantId, t, "ticket.updated");
        return t;
    }

    private void recordOutbox(UUID tenantId, TicketEntity t, String eventType) {
        try {
            Map<String, Object> meta = new HashMap<>();
            meta.put("eventId", UUID.randomUUID().toString());
            meta.put("tenantId", tenantId.toString());
            meta.put("occurredAt", OffsetDateTime.now().toString());
            meta.put("domain", "ticket");
            meta.put("type", eventType);
            meta.put("version", 1);
            meta.put("aggregateId", t.getId().toString());
            meta.put("aggregateType", "Ticket");

            Map<String, Object> data = new HashMap<>();
            data.put("ticketId", t.getId().toString());
            data.put("title", t.getTitle());
            data.put("status", t.getStatus().toString());
            data.put("description", t.getDescription());
            if (t.getAssignee() != null) {
                data.put("assignee", t.getAssignee());
            }
            data.put("createdAt", t.getCreatedAt());
            data.put("updatedAt", t.getUpdatedAt());

            Map<String, Object> payload = Map.of("meta", meta, "data", data);

            OutboxEntity entity = OutboxEntity.builder().tenantId(tenantId).topic(ticketTopic)
                    .recordKey(tenantId + ":" + t.getId()).payload(payload)
                    .createdAt(OffsetDateTime.now()).build();
            outboxRepo.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record outbox event", e);
        }
    }
}
