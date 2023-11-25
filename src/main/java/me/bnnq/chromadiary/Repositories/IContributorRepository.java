package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Contributor;
import org.springframework.data.repository.CrudRepository;

public interface IContributorRepository extends CrudRepository<Contributor, Long>
{
    Contributor findByUserIdAndDiaryId(Long userId, Long diaryId);
}
