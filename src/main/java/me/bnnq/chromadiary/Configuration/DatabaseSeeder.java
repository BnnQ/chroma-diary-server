package me.bnnq.chromadiary.Configuration;

import jakarta.transaction.Transactional;
import me.bnnq.chromadiary.Models.*;
import me.bnnq.chromadiary.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseSeeder {
    @Bean
    @Transactional
    CommandLineRunner initDatabase(IUserRepository userRepository, IDiaryRepository diaryRepository, ITagRepository tagRepository, IPostRepository postRepository, IAttachmentRepository attachmentRepository, IContributorRepository contributorRepository, IPermissionRepository permissionRepository) {
        return args -> {
            if (userRepository.count() == 0 && diaryRepository.count() == 0 && tagRepository.count() == 0 && postRepository.count() == 0 && attachmentRepository.count() == 0 && contributorRepository.count() == 0 && permissionRepository.count() == 0) {
                var user1 = new User(null, "John", "password", "John Doe", "avatarUrl", new ArrayList<>(), new ArrayList<>());
                var user2 = new User(null, "Jane", "password", "Jane Doe", "avatarUrl", new ArrayList<>(), new ArrayList<>());
                user1 = userRepository.save(user1);
                user2 = userRepository.save(user2);

                var tag1 = new Tag(null, "tag1", "#ff0000", new ArrayList<>());
                var tag2 = new Tag(null, "tag2", "#cc00dd", new ArrayList<>());
                tag1 = tagRepository.save(tag1);
                tag2 = tagRepository.save(tag2);

                var diary1 = new Diary(null, "Diary1", "#BF40BF", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), null, null, user1, List.of(tag1), new ArrayList<>(), new ArrayList<>());
                var diary2 = new Diary(null, "Diary2", "#FFFF00", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), null, null, user2, List.of(tag2), new ArrayList<>(), new ArrayList<>());
                diary1 = diaryRepository.save(diary1);
                diary2 = diaryRepository.save(diary2);

                var post1 = new Post(null, "<h2>content1</h2>", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), diary1, new ArrayList<>());
                var post2 = new Post(null, "<b>content2</b>", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), diary2, new ArrayList<>());
                post1 = postRepository.save(post1);
                post2 = postRepository.save(post2);

                var attachment1 = new Attachment(null, "https://img.freepik.com/free-photo/digital-painting-mountain-with-colorful-tree-foreground_1340-25699.jpg", true, false, null, null, post1);
                var attachment2 = new Attachment(null, "https://images.ctfassets.net/hrltx12pl8hq/4f6DfV5DbqaQUSw0uo0mWi/6fbcf889bdef65c5b92ffee86b13fc44/shutterstock_376532611.jpg?fit=fill&w=600&h=400", true, false, null, null, post2);
                var attachment3 = new Attachment(null, "https://mirrors.aliyun.com/android.googlesource.com/prebuilts/sdk/current/placeholder-api.txt", false, false, "placeholder-api.txt", 2350L, post2);
                attachment1 = attachmentRepository.save(attachment1);
                attachment2 = attachmentRepository.save(attachment2);
                attachment3 = attachmentRepository.save(attachment3);
            }
        };
    }
}
