package com.adesk.ticketsvc.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.adesk.ticketsvc.dto.TicketCreate;
import com.adesk.ticketsvc.dto.TicketUpdate;
import com.adesk.ticketsvc.model.TicketEntity;
import com.adesk.ticketsvc.model.TicketStatus;
import com.adesk.ticketsvc.outbox.OutboxEntity;
import com.adesk.ticketsvc.outbox.OutboxRepository;
import com.adesk.ticketsvc.repo.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper = new ObjectMapper();

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
        ticketRepo.save(t);
        recordOutbox(tenantId, t, "ticket.created");
        return t;
    }

    public List<TicketEntity> list(UUID tenantId, Integer limit, Integer offset,
            TicketStatus status, String assignee) {
        int l = Math.min(1000, Math.max(1, limit));
        int o = Math.max(0, offset);
        int page = o / l;
        var pageable = PageRequest.of(page, l, Sort.by(Sort.Direction.DESC, "createdAt"));
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

    private void recordOutbox(UUID tenantId, TicketEntity t, String eventName) {
        try {
            Map<String, Object> payload = Map.of("meta",
                    Map.of("eventId", UUID.randomUUID().toString(), "occurredAt",
                            OffsetDateTime.now().toString(), "tenantId", tenantId.toString(),
                            "domain", "ticket", "name", eventName, "version", 1, "aggregateId",
                            t.getId().toString(), "aggregateType", "Ticket"),
                    "data", Map.of("ticketId", t.getId().toString(), "title", t.getTitle(),
                            "status", t.getStatus().toString(), "assignee", t.getAssignee()));
            String json = mapper.writeValueAsString(payload);

            OutboxEntity entity = OutboxEntity.builder().tenantId(tenantId).topic(ticketTopic)
                    .recordKey(tenantId + ":" + t.getId()).payload(payload)
                    .createdAt(OffsetDateTime.now()).build();
            outboxRepo.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to record outbox event", e);
        }
    }
}
