package me.bnnq.chromadiary.Controllers;

import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.Tag;
import me.bnnq.chromadiary.Repositories.ITagRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tags")
@AllArgsConstructor
public class TagController
{
    private ITagRepository tagRepository;

    @GetMapping("/get/{search}")
    public ResponseEntity<List<Tag>> getTags(@PathVariable String search, @RequestParam int limit)
    {
        Pageable pageable = PageRequest.of(0, limit);
        var tags = tagRepository.searchByNameStartsWith(search, pageable);
        var tagsList = tags.getContent();
        return ResponseEntity.ok(tagsList);
    }
}
