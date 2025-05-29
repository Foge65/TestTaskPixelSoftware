package software.pxel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.UserEntity;
import software.pxel.repository.EmailRepository;

@Service
@RequiredArgsConstructor
public class DetailsServiceImpl implements UserDetailsService {
    private final EmailRepository emailRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmailEntity emailEntity = emailRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("EmailEntity not found"));

        UserEntity userEntity = emailEntity.getUserEntity();
        if (userEntity == null) {
            throw new UsernameNotFoundException("UserEntity not linked to email");
        }

        return User.builder().username(emailEntity.getEmail()).password(userEntity.getPassword()).build();
    }
}
