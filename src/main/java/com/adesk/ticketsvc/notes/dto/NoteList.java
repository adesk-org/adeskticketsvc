package com.adesk.ticketsvc.notes.dto;

import java.util.List;
import com.adesk.ticketsvc.notes.model.NoteEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record NoteList(

        @PositiveOrZero(message = "total must be greater than or equal to 0") int total,

        @Min(value = 1, message = "limit must be greater than 0") @Max(value = 1000,
                message = "limit must be less or equal to than 1000") int limit,

        @PositiveOrZero(message = "offset must be greater than or equal to 0") int offset,

        @Valid List<NoteEntity> items) {

    @AssertTrue(message = "items size must be less than or equal to limit")
    private boolean isItemsWithinLimit() {
        return items == null || items.size() <= limit;
    }
}
