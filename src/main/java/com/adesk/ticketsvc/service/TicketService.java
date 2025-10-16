package com.adesk.ticketsvc.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.adesk.ticketsvc.model.TicketEntity;
import com.adesk.ticketsvc.model.TicketStatus;
import com.adesk.ticketsvc.repo.TicketRepository;

@Service
public class TicketService {
    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
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
}
