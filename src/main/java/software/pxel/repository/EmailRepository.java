package software.pxel.repository;

import jakarta.persistence.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
@Cacheable
public interface EmailRepository extends CrudRepository<EmailEntity, Long> {
    Optional<EmailEntity> findByEmail(String email);

    List<EmailEntity> findAllByUserEntity_Id(Long userEntityId);

    List<EmailEntity> findAllByUserEntity(UserEntity user);
}
