package com.nexola.apiportfolio.services;

import com.nexola.apiportfolio.dto.EducationDTO;
import com.nexola.apiportfolio.dto.ProjectDTO;
import com.nexola.apiportfolio.dto.UserDTO;
import com.nexola.apiportfolio.dto.UserMinDTO;
import com.nexola.apiportfolio.entities.*;
import com.nexola.apiportfolio.projections.UserDetailsProjection;
import com.nexola.apiportfolio.repositories.*;
import com.nexola.apiportfolio.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private HeaderRepository headerRepository;

    @Autowired
    private FooterRepository footerRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserDTO getPortfolio(Long id) {
        User user = repository.getPortfolio(id);

        Header header = headerRepository.getHeader(id);
        Footer footer = footerRepository.getFooter(id);
        Education education = educationRepository.getEducation(id);
        Experience experience = experienceRepository.getExperience(id);

        user.setHeader(header);
        user.setFooter(footer);
        user.setEducation(education);
        user.setExperience(experience);

        return new UserDTO(user);
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return repository.findByEmail(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário inválido");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> list = repository.searchUserAndRolesByEmail(username);
        if (list.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(list.get(0).getPassword());
        for (UserDetailsProjection projection : list) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }

    @Transactional
    public UserMinDTO getMe() {
        User user = authenticated();
        return new UserMinDTO(user);
    }
}
