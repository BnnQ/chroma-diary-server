package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.bnnq.chromadiary.Models.Tag;
import org.springframework.lang.Nullable;

import java.sql.Date;

@Getter
@Setter
public class DiaryArchivationDto
{
    private Long id;
    private Date expiresAt;
    private Tag[] tags;

    public DiaryArchivationDto(Long id, Date expiresAt, @Nullable Tag[] tags)
    {
        this.id = id;
        this.expiresAt = expiresAt;
        this.tags = tags;
    }
}
