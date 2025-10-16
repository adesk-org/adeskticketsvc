package com.adesk.ticketsvc.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.adesk.ticketsvc.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService svc;

    public TicketController(TicketService svc) {
        this.svc = svc;
    }

    public Ticket
}
