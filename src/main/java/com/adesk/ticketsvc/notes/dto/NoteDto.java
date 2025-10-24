package com.adesk.ticketsvc.notes.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record NoteDto(@NotBlank(message = "id is required") UUID id,
        @NotBlank(message = "content is required") @Size(max = 50000,
                message = "content must be <= 50,000 characters") String content,
        @NotBlank(message = "authorName is required") @Size(max = 120,
                message = "content must be <= 120 characters") String authorName,
        @NotNull(message = "createdAt is required") @PastOrPresent(
                message = "createdAt cannot be in the future") OffsetDateTime createdAt,
        @NotNull(message = "updatedAt is required") @PastOrPresent(
                message = "updatedAt cannot be in the future") OffsetDateTime updatedAt) {
}
