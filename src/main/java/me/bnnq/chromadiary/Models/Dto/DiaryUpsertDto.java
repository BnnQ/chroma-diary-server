package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class DiaryUpsertDto
{
    private Long id;
    private String title;
    private String color;

    @Nullable
    private TagDto[] tags;

    @Nullable
    private TagCreateDto[] additionalTags;
}
