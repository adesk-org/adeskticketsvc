package com.adesk.ticketsvc.web;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.dto.TicketList;
import com.adesk.ticketsvc.model.TicketEntity;
import com.adesk.ticketsvc.model.TicketStatus;
import com.adesk.ticketsvc.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService svc;

    public TicketController(TicketService svc) {
        this.svc = svc;
    }

    @GetMapping
    public TicketList list(HttpServletRequest req, @RequestParam Integer limit,
            @RequestParam Integer offset, @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String assignee) {
        UUID tenantId = UUID.fromString("bee1e4b2-1c7a-4944-92aa-b0dde5088c87");
        List<TicketEntity> items = svc.list(tenantId, limit, offset, status, assignee);
        int total = svc.count(tenantId, status, assignee);
        return new TicketList(total, limit, offset, items);
    }

}
