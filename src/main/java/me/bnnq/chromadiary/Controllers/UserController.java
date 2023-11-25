package me.bnnq.chromadiary.Controllers;

import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.User;
import me.bnnq.chromadiary.Repositories.IUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
@AllArgsConstructor
public class UserController
{
    private final IUserRepository userRepository;

    @GetMapping("/get/byUsername/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username)
    {
        var user = userRepository.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get/byId/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id)
    {
        var user = userRepository.findById(id).orElse(null);
        return ResponseEntity.ok(user);
    }
}
