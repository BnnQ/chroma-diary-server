package me.bnnq.chromadiary.Models.Dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto {
    private Long id;
    private String name;
    private String color;

    public TagDto() {
    }

    public TagDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}

