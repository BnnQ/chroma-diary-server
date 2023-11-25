package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPostRepository extends CrudRepository<Post, Long>
{
    Page<Post> findByDiaryId(Long diaryId, Pageable pageable);
}
