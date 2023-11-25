package me.bnnq.chromadiary.Controllers;

import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.Attachment;
import me.bnnq.chromadiary.Models.Diary;
import me.bnnq.chromadiary.Models.Dto.PostUpsertDto;
import me.bnnq.chromadiary.Models.Post;
import me.bnnq.chromadiary.Repositories.IAttachmentRepository;
import me.bnnq.chromadiary.Repositories.IDiaryRepository;
import me.bnnq.chromadiary.Repositories.IPostRepository;
import me.bnnq.chromadiary.Services.FirebaseStorageService;
import me.bnnq.chromadiary.Utils.Files;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/posts")
@AllArgsConstructor
public class PostController
{
    private IPostRepository postRepository;
    private IDiaryRepository diaryRepository;
    private IAttachmentRepository attachmentRepository;
    private FirebaseStorageService firebaseStorageService;

    @GetMapping("/get/{diaryId}")
    public ResponseEntity<List<Post>> getPosts(@PathVariable Long diaryId, @RequestParam int page, @RequestParam int itemsPerPage)
    {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        var posts = postRepository.findByDiaryId(diaryId, pageable);
        var postsList = posts.getContent();
        return ResponseEntity.ok(postsList);
        //return ResponseEntity.ok(StreamSupport.stream(postRepository.findAll().spliterator(), false).toList());
    }

    @RequestMapping(path = "/post", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> createPost(@ModelAttribute PostUpsertDto postUpsertDto)
    {
        Post post = new Post();
        post.setContent(postUpsertDto.getContent());

        var diary = diaryRepository.findById(postUpsertDto.getDiaryId()).orElse(null);
        if (diary != null)
        {
            post.setDiary(diary);
            diary.getPosts().add(post);
        }

        final Post savedPost = postRepository.save(post);

        ArrayList<Attachment> attachments = new ArrayList<>();
        if (postUpsertDto.getMediaAttachments() != null)
        {
            Arrays.stream(postUpsertDto.getMediaAttachments()).forEach(file ->
            {
                try
                {
                    Attachment attachment = new Attachment();
                    attachment.setUrl(firebaseStorageService.uploadFile(file));
                    attachment.setPost(savedPost);

                    if (Files.getFileExtension(file).equals("png") || Files.getFileExtension(file).equals("jpg") || Files.getFileExtension(file).equals("jpeg"))
                        attachment.setImage(true);
                    else if (Files.getFileExtension(file).equals("mp4") || Files.getFileExtension(file).equals("webm") || Files.getFileExtension(file).equals("ogg"))
                        attachment.setVideo(true);

                    attachment = attachmentRepository.save(attachment);
                    attachments.add(attachment);
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
            });
        }

        if (postUpsertDto.getFileAttachments() != null)
        {
            Arrays.stream(postUpsertDto.getFileAttachments()).forEach(file ->
            {
                try
                {
                    Attachment attachment = new Attachment();
                    attachment.setUrl(firebaseStorageService.uploadFile(file));
                    attachment.setName(file.getOriginalFilename());
                    attachment.setSize(file.getSize());
                    attachment.setPost(savedPost);

                    attachment = attachmentRepository.save(attachment);
                    attachments.add(attachment);
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
            });
        }

        post.setAttachments(attachments);
        post = postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    @RequestMapping(path = "/put", method = RequestMethod.PUT, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> editPost(@ModelAttribute PostUpsertDto postUpsertDto) {
        Post post = postRepository.findById(postUpsertDto.getId()).orElse(null);
        if (post == null)
            return ResponseEntity.notFound().build();

        post.setContent(postUpsertDto.getContent());

        Diary diary = diaryRepository.findById(postUpsertDto.getDiaryId()).orElse(null);
        if (diary != null) {
            post.setDiary(diary);
            diary.getPosts().add(post);
        }

        // Remove existing media attachments if new ones are provided
        if (postUpsertDto.getMediaAttachments() != null) {
            List<Attachment> existingAttachments = new ArrayList<>(post.getAttachments());
            for (Attachment attachment : existingAttachments) {
                if (attachment.isImage() || attachment.isVideo()) {
                    post.getAttachments().remove(attachment);
                    attachment.setPost(null);
                    attachmentRepository.delete(attachment);
                }
            }
        }

        // Remove existing file attachments if new ones are provided
        if (postUpsertDto.getFileAttachments() != null) {
            List<Attachment> existingAttachments = new ArrayList<>(post.getAttachments());
            for (Attachment attachment : existingAttachments) {
                if (!attachment.isImage() && !attachment.isVideo()) {
                    post.getAttachments().remove(attachment);
                    attachment.setPost(null);
                    attachmentRepository.delete(attachment);
                }
            }
        }

        post = postRepository.save(post);

        ArrayList<Attachment> attachments = new ArrayList<>();
        if (postUpsertDto.getMediaAttachments() != null) {
            for (MultipartFile file : postUpsertDto.getMediaAttachments()) {
                try {
                    Attachment attachment = new Attachment();
                    attachment.setUrl(firebaseStorageService.uploadFile(file));
                    attachment.setPost(post);

                    if (Files.getFileExtension(file).equals("png") || Files.getFileExtension(file).equals("jpg") || Files.getFileExtension(file).equals("jpeg"))
                        attachment.setImage(true);
                    else if (Files.getFileExtension(file).equals("mp4") || Files.getFileExtension(file).equals("webm") || Files.getFileExtension(file).equals("ogg"))
                        attachment.setVideo(true);

                    attachment = attachmentRepository.save(attachment);
                    attachments.add(attachment);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (postUpsertDto.getFileAttachments() != null) {
            for (MultipartFile file : postUpsertDto.getFileAttachments()) {
                try {
                    Attachment attachment = new Attachment();
                    attachment.setUrl(firebaseStorageService.uploadFile(file));
                    attachment.setName(file.getOriginalFilename());
                    attachment.setSize(file.getSize());
                    attachment.setPost(post);

                    attachment = attachmentRepository.save(attachment);
                    attachments.add(attachment);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        post.getAttachments().addAll(attachments);
        post = postRepository.save(post);
        return ResponseEntity.ok(post);
    }






    @DeleteMapping("/delete/{postId}")
    public void deletePost(@PathVariable Long postId)
    {
        var post = postRepository.findById(postId).orElse(null);
        if (post != null)
        {
            var diary = post.getDiary();
            if (diary != null)
            {
                diary.getPosts().remove(post);
                diaryRepository.save(diary);
            }

            post.setDiary(null);
            postRepository.deleteById(postId);
        }

    }

}
