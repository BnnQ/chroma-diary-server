package me.bnnq.chromadiary.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagCreateDto
{
    private String name;
    private String color;
}
