package com.adesk.ticketsvc.dto;

import com.adesk.ticketsvc.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdate {

    private TicketStatus status;

    private String assignee;
}
