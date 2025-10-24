package com.adesk.ticketsvc.notes.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notes")
public class NoteEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "tenantId is required")
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @NotNull(message = "ticketId is required")
    @Column(name = "ticket_id", nullable = false, updatable = false)
    private UUID ticketId;

    @Generated(event = EventType.INSERT)
    @Column(name = "seq", insertable = false, updatable = false)
    private Integer seq;

    @NotBlank(message = "content is required")
    @Size(max = 50_000, message = "content must be <= 50,000 characters")
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @NotBlank(message = "authorName is required")
    @Size(max = 120, message = "authorName must be <= 120 characters")
    @Column(name = "author_name", nullable = false, length = 120)
    private String authorName;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
