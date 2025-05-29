package software.pxel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.PhoneEntity;
import software.pxel.entity.UserEntity;
import software.pxel.repository.EmailRepository;
import software.pxel.repository.PhoneRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final SearchService searchService;

    @Transactional
    public void addPhone(String newPhone) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        UserEntity userEntity;
        if (currentEmail.isPresent()) {
            userEntity = currentEmail.get().getUserEntity();
        } else {
            throw new RuntimeException("Phone not registered in phone_data");
        }

        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setUserEntity(userEntity);
        phoneEntity.setPhone(newPhone);

        phoneRepository.save(phoneEntity);
        searchService.addUserToIndex(userEntity);
    }

    @Transactional
    public void deletePhone(String phone) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        if (currentEmail.isPresent()) {
            List<PhoneEntity> allUserPhones = phoneRepository.findAllByUserEntity_Id(currentEmail.get().getUserEntity().getId());
            if (allUserPhones.size() > 1) {
                PhoneEntity phoneEntity = phoneRepository.findByPhone(phone)
                        .orElseThrow(() -> new RuntimeException("Phone doesn't exist"));
                try {
                    phoneRepository.delete(phoneEntity);
                    searchService.deleteUserFromIndex(phoneEntity.getUserEntity());
                } catch (RuntimeException e) {
                    throw new RuntimeException("Error deleting phone", e);
                }
            } else {
                throw new RuntimeException("Can't delete only one have phone");
            }
        }
    }

    @Transactional
    public void changePhone(String oldPhone, String newPhone) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        if (currentEmail.isPresent()) {
            List<PhoneEntity> allUserPhones = phoneRepository.findAllByUserEntity_Id(currentEmail.get().getUserEntity().getId());
            allUserPhones.forEach(phoneEntity -> {
                if (phoneEntity.getPhone().equals(oldPhone)) {
                    phoneEntity.setPhone(newPhone);
                    phoneRepository.save(phoneEntity);
                    searchService.addUserToIndex(phoneEntity.getUserEntity());
                }
            });
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
