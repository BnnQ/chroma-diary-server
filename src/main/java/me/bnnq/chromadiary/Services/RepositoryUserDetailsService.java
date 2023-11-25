package me.bnnq.chromadiary.Services;

import lombok.AllArgsConstructor;
import me.bnnq.chromadiary.Repositories.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@AllArgsConstructor
public class RepositoryUserDetailsService implements UserDetailsService
{
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        var user = userRepository.findByUsername(username);
        user.setPassword("{noop}" + user.getPassword());
        return user;
    }
}
