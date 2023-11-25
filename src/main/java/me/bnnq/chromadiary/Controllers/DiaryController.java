package me.bnnq.chromadiary.Controllers;

import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.Diary;
import me.bnnq.chromadiary.Models.Dto.*;
import me.bnnq.chromadiary.Models.Tag;
import me.bnnq.chromadiary.Models.User;
import me.bnnq.chromadiary.Repositories.IDiaryRepository;
import me.bnnq.chromadiary.Repositories.ITagRepository;
import me.bnnq.chromadiary.Repositories.IUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.*;

@RestController
@RequestMapping(value = "/api/diaries")
@AllArgsConstructor
public class DiaryController
{
    private IDiaryRepository diaryRepository;
    private ITagRepository tagRepository;
    private IUserRepository userRepository;

    @GetMapping("/get/user/{userId}")
    public ResponseEntity<List<DiaryMetadata>> getUserDiaries(@PathVariable Long userId)
    {
        var diaries = diaryRepository.getUserDiaries(userId);
        var diariesMapped = diaries.stream().map(diary -> new DiaryMetadata(diary.getId(), diary.getTitle(), diary.getColor(), diary.getCreatedAt(), diary.getUpdatedAt(), diary.getTags().stream().map(tag -> new LightweightTag(tag.getName(), tag.getColor())).toList()));
        return ResponseEntity.ok((diariesMapped.toList()));
    }

    @GetMapping("/get/user/shared/{userId}")
    public ResponseEntity<List<SharedDiaryMetadata>> getSharedDiaries(@PathVariable Long userId)
    {
        var diaries = diaryRepository.getUserSharedDiaries(userId);
        var diariesMapped = diaries.stream().map(diary -> new SharedDiaryMetadata(diary.getId(), diary.getTitle(), diary.getColor(), diary.getCreatedAt(), diary.getUpdatedAt(), new UserDto(diary.getAuthor().getId(), diary.getAuthor().getUsername(), diary.getAuthor().getPassword(), diary.getAuthor().getFullName()), diary.getTags()));
        return ResponseEntity.ok(diariesMapped.toList());
    }

    @GetMapping("/get/user/archived/{authorId}")
    public ResponseEntity<List<ArchivedDiaryMetadata>> getArchivedDiaries(@PathVariable Long authorId)
    {
        var diaries = diaryRepository.getArchivedDiariesByAuthorId(authorId);
        var diariesMapped = diaries.stream().map(diary -> new ArchivedDiaryMetadata(diary.getId(), diary.getTitle(), diary.getColor(), diary.getCreatedAt(), diary.getArchivedAt(), diary.getExpiresAt(), diary.getTags()));
        return ResponseEntity.ok(diariesMapped.toList());
    }

    @GetMapping("/get/{diaryId}")
    public ResponseEntity<LightweightDiary> getDiary(@PathVariable Long diaryId)
    {
        var diaryEntry = diaryRepository.getDiaryById(diaryId);
        if (diaryEntry.isEmpty())
            return ResponseEntity.notFound().build();

        var diary = diaryEntry.get();
        var lightweightDiary = new LightweightDiary(diary.getId(), diary.getTitle());
        return ResponseEntity.ok(lightweightDiary);
    }

    @GetMapping("/get/archived/{diaryId}")
    public ResponseEntity<LightweightDiary> getArchivedDiary(@PathVariable Long diaryId)
    {
        var diaryEntry = diaryRepository.getArchivedDiaryById(diaryId);
        if (diaryEntry.isEmpty())
            return ResponseEntity.notFound().build();

        var diary = diaryEntry.get();
        var lightweightDiary = new LightweightDiary(diary.getId(), diary.getTitle());
        return ResponseEntity.ok(lightweightDiary);
    }

    @PostMapping("/post")
    public ResponseEntity<DiaryMetadata> createDiary(@RequestBody DiaryUpsertDto model, Authentication authentication)
    {
        Diary diary = new Diary();
        diary.setId(model.getId());
        diary.setTitle(model.getTitle());
        diary.setColor(model.getColor());
        var author = userRepository.findByUsername(authentication.getName());
        diary.setAuthor(author);

        diary = diaryRepository.save(diary);
        author.getDiaries().add(diary);
        userRepository.save(author);

        if (model.getTags() != null)
        {
            for (TagDto tag : model.getTags())
            {
                var tagEntry = tagRepository.findById(tag.getId()).get();
                diary.getTags().add(tagEntry);
            }
        }

        if (model.getAdditionalTags() != null)
        {
            for (TagCreateDto tag : model.getAdditionalTags())
            {
                Tag newTag = new Tag();
                newTag.setName(tag.getName());
                newTag.setColor(tag.getColor());
                tagRepository.save(newTag);
                diary.getTags().add(newTag);
            }
        }

        diary.setCreatedAt(new Date(System.currentTimeMillis()));
        diary.setUpdatedAt(new Date(System.currentTimeMillis()));
        var diaryEntry = diaryRepository.save(diary);
        return ResponseEntity.ok(new DiaryMetadata(diaryEntry.getId(), diaryEntry.getTitle(), diaryEntry.getColor(), diaryEntry.getCreatedAt(), diaryEntry.getUpdatedAt(), diaryEntry.getTags().stream().map(tag -> new LightweightTag(tag.getName(), tag.getColor())).toList()));
    }

    @PutMapping("/put")
    public ResponseEntity<DiaryMetadata> editDiary(@RequestBody DiaryUpsertDto model)
    {
        Optional<Diary> diaryEntry = diaryRepository.findById(model.getId());

        if (!diaryEntry.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        Diary diary = diaryEntry.get();
        diary.setTitle(model.getTitle());
        diary.setColor(model.getColor());

        // Remove all existing tags from the diary
        for (Tag tag : diary.getTags())
        {
            tag.getDiaries().remove(diary);
            tagRepository.save(tag);
        }
        diary.getTags().clear();

        // Add existing tags to the diary
        if (model.getTags() != null)
        {
            for (TagDto tagDto : model.getTags())
            {
                Tag tag = tagRepository.findById(tagDto.getId()).get();
                tag.getDiaries().add(diary);
                diary.getTags().add(tag);
                tagRepository.save(tag);
            }
        }

        // Create and add new tags to the diary
// Create and add new tags to the diary
        if (model.getAdditionalTags() != null)
        {
            for (TagCreateDto tagDto : model.getAdditionalTags())
            {
                Tag newTag = new Tag();
                newTag.setName(tagDto.getName());
                newTag.setColor(tagDto.getColor());
                newTag = tagRepository.save(newTag); // Save the new tag first

                newTag.setDiaries(new ArrayList<>());
                newTag.getDiaries().add(diary);
                diary.getTags().add(newTag);

                diary = diaryRepository.save(diary); // Save the diary after adding the new tag
            }
        }


        diary.setUpdatedAt(new Date(System.currentTimeMillis()));
        diaryRepository.save(diary);

        return ResponseEntity.ok(new DiaryMetadata(diary.getId(), diary.getTitle(), diary.getColor(), diary.getCreatedAt(), diary.getUpdatedAt(), diary.getTags().stream().map(tag -> new LightweightTag(tag.getName(), tag.getColor())).toList()));
    }

    @PutMapping("put/archive")
    public ResponseEntity<Void> archivateDiary(@RequestBody DiaryArchivationDto model)
    {
        var diaryEntry = diaryRepository.findById(model.getId());
        if (diaryEntry.isPresent())
        {
            var diary = diaryEntry.get();
            diary.setArchivedAt(new Date(System.currentTimeMillis()));
            diary.setExpiresAt(model.getExpiresAt());
            diaryRepository.save(diary);
            return ResponseEntity.ok().build();
        } else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("put/unarchive/{diaryId}")
    public ResponseEntity<Void> unarchivateDiary(@PathVariable Long diaryId)
    {
        var diaryEntry = diaryRepository.findById(diaryId);
        if (diaryEntry.isPresent())
        {
            var diary = diaryEntry.get();
            diary.setArchivedAt(null);
            diary.setExpiresAt(null);
            diaryRepository.save(diary);
            return ResponseEntity.ok().build();
        } else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("delete/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId)
    {
        // Find the diary
        Optional<Diary> diaryOpt = diaryRepository.findById(diaryId);
        if (diaryOpt.isPresent()) {
            Diary diary = diaryOpt.get();

            // Find the author
            User author = diary.getAuthor();

            // Detach the diary from the author
            author.getDiaries().remove(diary);
            diary.setAuthor(null);
            userRepository.save(author);

            //Detach tags
            for (Tag tag : new HashSet<>(diary.getTags()))
            {
                tag.getDiaries().remove(diary);
                diary.getTags().remove(tag);
                tagRepository.save(tag);
            }

            //Now delete diary
            diary = diaryRepository.save(diary);
            diaryRepository.delete(diary);
        }

        return ResponseEntity.ok().build();
    }



}
