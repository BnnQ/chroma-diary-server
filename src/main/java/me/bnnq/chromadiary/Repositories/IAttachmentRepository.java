package me.bnnq.chromadiary.Repositories;

import me.bnnq.chromadiary.Models.Attachment;
import org.springframework.data.repository.CrudRepository;

public interface IAttachmentRepository extends CrudRepository<Attachment, Long>
{
}
