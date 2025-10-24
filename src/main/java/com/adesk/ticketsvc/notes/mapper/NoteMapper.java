package com.adesk.ticketsvc.notes.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.adesk.ticketsvc.notes.dto.NoteDto;
import com.adesk.ticketsvc.notes.model.NoteEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {
    NoteDto toDto(NoteEntity entity);

    List<NoteDto> toDtoList(List<NoteEntity> entities);
}
