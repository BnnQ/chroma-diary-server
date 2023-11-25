package me.bnnq.chromadiary.Controllers;

import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Models.Dto.UserRegistrationDto;
import me.bnnq.chromadiary.Models.User;
import me.bnnq.chromadiary.Repositories.IUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth")
@AllArgsConstructor
public class AuthenticationController
{
    JwtEncoder encoder;
    private final IUserRepository userRepository;


    @PostMapping("/register")
    public void register(@RequestBody UserRegistrationDto model)
    {
        var user = new User(null, model.getEmail(), model.getPassword(), model.getFullName(), null, null, null);
        userRepository.save(user);
    }
    @PostMapping("/login")
    public String login(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @GetMapping("/get/currentUserId")
    public Long getCurrentUserId(Authentication authentication)
    {
        var user = userRepository.findByUsername(authentication.getName());
        return user.getId();
    }
}
