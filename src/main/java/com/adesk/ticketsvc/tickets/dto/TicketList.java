package com.adesk.ticketsvc.tickets.dto;

import java.util.List;
import com.adesk.ticketsvc.tickets.model.TicketEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketList {

    private int total;

    private int limit;

    private int offset;

    private List<TicketEntity> items;
}
