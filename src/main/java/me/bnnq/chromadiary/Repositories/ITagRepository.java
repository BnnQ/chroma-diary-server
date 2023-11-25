package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ITagRepository extends CrudRepository<Tag, Long>
{
    Page<Tag> searchByNameStartsWith(String search, Pageable pageable);
}
