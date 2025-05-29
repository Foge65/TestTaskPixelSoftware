package software.pxel.repository;

import jakarta.persistence.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import software.pxel.entity.PhoneEntity;
import software.pxel.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
@Cacheable
public interface PhoneRepository extends CrudRepository<PhoneEntity, Long> {
    Optional<PhoneEntity> findByPhone(String phone);

    List<PhoneEntity> findAllByUserEntity_Id(Long userEntityId);

    List<PhoneEntity> findAllByUserEntity(UserEntity user);
}
