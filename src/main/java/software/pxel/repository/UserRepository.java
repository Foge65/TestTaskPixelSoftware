package software.pxel.repository;

import jakarta.persistence.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import software.pxel.entity.UserEntity;

@Repository
@Cacheable
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
