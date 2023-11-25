package me.bnnq.chromadiary.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Permission
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean canRead;
    private boolean canWrite;
    private boolean canEdit;
    private boolean canDelete;

    @OneToOne(mappedBy = "permissions", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
    private Contributor contributor;
}
