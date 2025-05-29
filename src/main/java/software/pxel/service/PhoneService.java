package software.pxel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final SearchService searchService;

    @Transactional
    public void addPhone(String newPhone) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        UserEntity userEntity = new UserEntity();
        if (currentEmail.isPresent()) {
            userEntity = currentEmail.get().getUserEntity();
        } else {
            log.error("Could not find phone for user {}", currentUser);
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
                phoneRepository.delete(phoneEntity);
                searchService.deleteUserFromIndex(phoneEntity.getUserEntity());
            }
        } else {
            log.error("Could not delete phone {}", phone);
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
        } else {
            log.error("Phone not registered in phone_data {}", currentEmail);
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
