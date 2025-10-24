package com.adesk.ticketsvc.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdateNoteRequest(

        @NotBlank(message = "content is required") @Size(max = 50000,
                message = "content must be less than 50000 characters") String content) {
}
