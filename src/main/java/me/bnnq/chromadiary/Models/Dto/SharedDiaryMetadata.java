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
public class SharedDiaryMetadata
{
    private Long id;
    private String title;
    private String color;
    private Date createdAt;
    private Date updatedAt;

    private UserDto author;

    @Nullable
    private List<Tag> tags;
}
