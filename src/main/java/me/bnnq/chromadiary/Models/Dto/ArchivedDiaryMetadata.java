package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.bnnq.chromadiary.Models.Tag;
import org.springframework.lang.Nullable;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ArchivedDiaryMetadata
{
    private Long id;
    private String title;
    private String color;
    private Date createdAt;
    private Date archivedAt;
    private Date expiresAt;

    @Nullable
    private List<Tag> tags;
}

