package com.adesk.ticketsvc.tickets.dto;

import com.adesk.ticketsvc.tickets.model.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdate {

    // Regular updatable fields
    private TicketStatus status;

    private String assignee;

    // Internal presence-tracking flags
    @JsonIgnore
    private boolean statusPresent = false;

    @JsonIgnore
    private boolean assigneePresent = false;

    // Custom setters mark the field as present if it exists in JSON
    @JsonSetter("status")
    public void setStatus(TicketStatus status) {
        this.statusPresent = true;
        this.status = status;
    }

    @JsonSetter("assignee")
    public void setAssignee(String assignee) {
        this.assigneePresent = true;
        this.assignee = assignee;
    }
}
