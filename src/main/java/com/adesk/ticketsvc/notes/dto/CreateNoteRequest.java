package com.adesk.ticketsvc.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateNoteRequest(

        @NotBlank(message = "content is required") @Size(max = 50000,
                message = "content must be <= 50,000 characters") String content,

        @NotBlank(message = "authorName is required") @Size(max = 120,
                message = "authorName must be <= 120 characters") String authorName) {
}
