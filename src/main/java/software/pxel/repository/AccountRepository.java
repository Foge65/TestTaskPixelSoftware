package software.pxel.repository;

import jakarta.persistence.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import software.pxel.entity.AccountEntity;

import java.util.Optional;

@Repository
@Cacheable
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByUserEntity_Id(Long id);
}
