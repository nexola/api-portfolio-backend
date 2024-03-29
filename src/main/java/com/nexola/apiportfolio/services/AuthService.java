package com.nexola.apiportfolio.services;

import com.nexola.apiportfolio.models.dto.EmailDTO;
import com.nexola.apiportfolio.models.dto.NewPasswordDTO;
import com.nexola.apiportfolio.models.entities.PasswordRecover;
import com.nexola.apiportfolio.models.entities.User;
import com.nexola.apiportfolio.repositories.PasswordRecoverRepository;
import com.nexola.apiportfolio.repositories.UserRepository;
import com.nexola.apiportfolio.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverURI;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    public void createRecoverToken(EmailDTO body) {
        User user = userRepository.findByEmail(body.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado")
        );

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(UUID.randomUUID().toString());
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        entity = passwordRecoverRepository.save(entity);

        String bodyEmail = "Acesse o link para definir uma nova senha\n\n"
                + recoverURI + entity.getToken() + "\n\nVálido por " + tokenMinutes + " minutos";

        emailService.sendEmail(entity.getEmail(), "Recuperação de senha", bodyEmail);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido");
        }

        User user = userRepository.findByEmail(result.get(0).getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado")
        );
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }
}
