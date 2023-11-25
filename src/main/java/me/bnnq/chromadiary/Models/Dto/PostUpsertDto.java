package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class PostUpsertDto
{
    public Long id;
    public Long diaryId;
    public String content;

    @Nullable
    public MultipartFile[] mediaAttachments;

    @Nullable
    public MultipartFile[] fileAttachments;
}
