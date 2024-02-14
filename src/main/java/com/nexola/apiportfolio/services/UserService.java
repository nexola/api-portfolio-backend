package com.nexola.apiportfolio.services;

import com.nexola.apiportfolio.entities.Role;
import com.nexola.apiportfolio.entities.User;
import com.nexola.apiportfolio.projections.UserDetailsProjection;
import com.nexola.apiportfolio.repositories.RoleRepository;
import com.nexola.apiportfolio.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

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
}