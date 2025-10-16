package com.adesk.ticketsvc.model;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

}
