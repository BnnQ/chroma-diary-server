package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Permission;
import org.springframework.data.repository.CrudRepository;

public interface IPermissionRepository extends CrudRepository<Permission, Long>
{
}
