package com.adesk.ticketsvc.notes.dto;

import java.util.List;
import com.adesk.ticketsvc.notes.model.NoteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteList {

    private int total;

    private int limit;

    private int offset;

    private List<NoteEntity> items;
}
