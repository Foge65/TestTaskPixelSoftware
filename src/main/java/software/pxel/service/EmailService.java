package software.pxel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.UserEntity;
import software.pxel.repository.EmailRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;
    private final SearchService searchService;

    @Transactional
    public void addEmail(String newEmail) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        UserEntity userEntity;
        if (currentEmail.isPresent()) {
            userEntity = currentEmail.get().getUserEntity();
        } else {
            throw new RuntimeException("Email not registered in email_data");
        }

        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setUserEntity(userEntity);
        emailEntity.setEmail(newEmail);

        emailRepository.save(emailEntity);
        searchService.addUserToIndex(userEntity);
    }

    @Transactional
    public void deleteEmail(String email) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        if (currentEmail.isPresent()) {
            List<EmailEntity> allUserEmails = emailRepository.findAllByUserEntity_Id(currentEmail.get().getUserEntity().getId());
            if (allUserEmails.size() > 1) {
                EmailEntity emailEntity = emailRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email doesn't exist"));
                try {
                    emailRepository.delete(emailEntity);
                    searchService.deleteUserFromIndex(emailEntity.getUserEntity());
                } catch (RuntimeException e) {
                    throw new RuntimeException("Error deleting entity", e);
                }
            } else {
                throw new RuntimeException("Can't delete only one have email");
            }
        }
    }

    @Transactional
    public void changeEmail(String oldEmail, String newEmail) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        if (currentEmail.isPresent()) {
            List<EmailEntity> allUserEmails = emailRepository.findAllByUserEntity_Id(currentEmail.get().getUserEntity().getId());
            allUserEmails.forEach(emailEntity -> {
                if (emailEntity.getEmail().equals(oldEmail)) {
                    emailEntity.setEmail(newEmail);
                    emailRepository.save(emailEntity);
                    searchService.addUserToIndex(emailEntity.getUserEntity());
                }
            });
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
