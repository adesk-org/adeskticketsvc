package com.adesk.ticketsvc.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.adesk.ticketsvc.dto.TicketCreate;
import com.adesk.ticketsvc.dto.TicketUpdate;
import com.adesk.ticketsvc.model.TicketEntity;
import com.adesk.ticketsvc.model.TicketStatus;
import com.adesk.ticketsvc.repo.TicketRepository;

@Service
public class TicketService {
    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
    }

    public TicketEntity get(UUID tenantId, UUID ticketId) {
        return repo.findByIdAndTenantId(ticketId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }

    public TicketEntity create(UUID tenantId, TicketCreate req) {
        TicketEntity t = new TicketEntity();
        t.setTenantId(tenantId);
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setAssignee(req.getAssignee());
        t.setStatus(TicketStatus.OPEN);
        return repo.save(t);
    }

    public List<TicketEntity> list(UUID tenantId, Integer limit, Integer offset,
            TicketStatus status, String assignee) {
        int l = Math.min(1000, Math.max(1, limit));
        int o = Math.max(0, offset);
        int page = o / l;
        var pageable = PageRequest.of(page, l, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repo.pageByFilters(tenantId, status, assignee, pageable).getContent();
    }

    public int count(UUID tenantId, TicketStatus status, String assignee) {
        return repo.countByFilters(tenantId, status, assignee);
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

        return repo.save(t);
    }
}
