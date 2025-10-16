package com.adesk.ticketsvc.notes.dto;

import jakarta.validation.constraints.Size;


public record CreateNoteRequest(@Size(max = 50000) String content,
                @Size(max = 120) String authorName) {

}
