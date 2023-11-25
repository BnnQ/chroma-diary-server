package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.bnnq.chromadiary.Models.Permission;

@Getter
@Setter
@AllArgsConstructor
public class ContributorDto
{
    private Long id;
    private Permission permissions;
}
