package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Diary;
import me.bnnq.chromadiary.Models.Dto.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDiaryRepository extends CrudRepository<Diary, Long>
{
    List<Diary> findAllByAuthorId(Long authorId);

    @Query("SELECT d FROM Diary d WHERE d.id = :diaryId")
    Optional<Diary> getDiaryById(@Param("diaryId") Long diaryId);

    @Query("SELECT d FROM Diary d WHERE d.id = :diaryId AND d.archivedAt IS NOT NULL")
    Optional<Diary> getArchivedDiaryById(@Param("diaryId") Long diaryId);

    @Query("select d FROM Diary d WHERE d.author.id = :authorId AND d.archivedAt IS NOT NULL")
    List<Diary> getArchivedDiariesByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT d FROM Diary d JOIN d.contributors c WHERE c.user.id = :userId")
    List<Diary> getUserSharedDiaries(@Param("userId") Long userId);

    @Query(value = "SELECT d FROM Diary d WHERE d.author.id = :userId AND d.expiresAt IS NULL")
    List<Diary> getUserDiaries(Long userId);
}
