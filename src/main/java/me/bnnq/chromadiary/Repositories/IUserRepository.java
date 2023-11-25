package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.User;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User, Long>
{
    User findByUsername(String username);
}
