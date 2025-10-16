package com.adesk.ticketsvc.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.dto.TicketList;
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

    private String getTenantId(HttpServeletRequest req) {
        return req.getAttribute()
    }

    @GetMapping
    public TicketList list(HttpServletRequest req, @RequestParam Integer limit,
            @RequestParam Integer offset, @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String assignee) {

    }
}
