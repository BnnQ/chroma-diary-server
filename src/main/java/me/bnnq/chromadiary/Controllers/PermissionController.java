package me.bnnq.chromadiary.Controllers;
import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.Contributor;
import me.bnnq.chromadiary.Models.Diary;
import me.bnnq.chromadiary.Models.Dto.ContributorDto;
import me.bnnq.chromadiary.Models.Permission;
import me.bnnq.chromadiary.Models.User;
import me.bnnq.chromadiary.Repositories.IContributorRepository;
import me.bnnq.chromadiary.Repositories.IDiaryRepository;
import me.bnnq.chromadiary.Repositories.IPermissionRepository;
import me.bnnq.chromadiary.Repositories.IUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController 
{
    private IUserRepository userRepository;
    private IDiaryRepository diaryRepository;
    private IContributorRepository contributorRepository;
    private IPermissionRepository permissionRepository;

    @GetMapping("/{userId}/{diaryId}")
    public ResponseEntity<Permission> getUserDiaryPermissions(@PathVariable String userId, @PathVariable Long diaryId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
        Diary diary = diaryRepository.findById(diaryId).orElse(null);

        if (user == null || diary == null) {
            return null;
        }

        Contributor contributor = contributorRepository.findByUserIdAndDiaryId(user.getId(), diary.getId());

        if (contributor == null) {
            return null;
        }

        Permission permission = contributor.getPermissions();
        return ResponseEntity.ok(new Permission(null, permission.isCanRead(), permission.isCanWrite(), permission.isCanEdit(), permission.isCanDelete(), contributor));
    }

    @GetMapping("/diary/{diaryId}/contributors")
    public ResponseEntity<List<Contributor>> getDiaryContributors(@PathVariable Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElse(null);

        if (diary == null) {
            return null;
        }

        return ResponseEntity.ok(diary.getContributors());
    }

    @PostMapping("/diary/{diaryId}/contributor/{username}")
    public ResponseEntity<Contributor> addDiaryContributor(Authentication authentication, @PathVariable Long diaryId, @PathVariable String username) {
        if (Objects.equals(authentication.getName(), username))
            return ResponseEntity.badRequest().build();

        User user = userRepository.findByUsername(username);
        Diary diary = diaryRepository.findById(diaryId).orElse(null);

        if (user == null || diary == null) {
            return null;
        }

        Contributor contributor = new Contributor();
        contributor.setUser(user);
        contributor.setDiary(diary);
        contributor = contributorRepository.save(contributor);

        var permissions = new Permission(null, false, false, false, false, contributor);
        permissions = permissionRepository.save(permissions);
        contributor.setPermissions(permissions);
        // Add the contributor to the user's and diary's list of contributors
        user.getContributions().add(contributor);
        diary.getContributors().add(contributor);

        userRepository.save(user);
        diaryRepository.save(diary);

        return ResponseEntity.ok(contributorRepository.save(contributor));
    }

    @DeleteMapping("/contributor/{contributorId}")
    public void removeDiaryContributor(@PathVariable Long contributorId) {
        Contributor contributor = contributorRepository.findById(contributorId).orElse(null);

        if (contributor == null) {
            return;
        }

        // Remove the contributor from the user's and diary's list of contributors
        User user = contributor.getUser();
        Diary diary = contributor.getDiary();
        user.getContributions().remove(contributor);
        diary.getContributors().remove(contributor);

        contributor.setUser(null);
        contributor.setDiary(null);
        userRepository.save(user);
        diaryRepository.save(diary);

        contributorRepository.deleteById(contributorId);
    }

    @PutMapping("/contributor")
    public ResponseEntity<Contributor> changeDiaryPermissions(@RequestBody ContributorDto contributor) {
        Contributor existingContributor = contributorRepository.findById(contributor.getId()).orElse(null);

        if (existingContributor == null) {
            return null;
        }

        existingContributor.setPermissions(contributor.getPermissions());

        return ResponseEntity.ok(contributorRepository.save(existingContributor));
    }
}
