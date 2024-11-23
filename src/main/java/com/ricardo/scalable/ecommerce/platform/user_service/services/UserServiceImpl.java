package com.ricardo.scalable.ecommerce.platform.user_service.services;

import com.ricardo.scalable.ecommerce.platform.user_service.entities.Role;
import com.ricardo.scalable.ecommerce.platform.user_service.entities.User;
import com.ricardo.scalable.ecommerce.platform.user_service.exceptions.PasswordDoNotMatchException;
import com.ricardo.scalable.ecommerce.platform.user_service.repositories.RoleRepository;
import com.ricardo.scalable.ecommerce.platform.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(getRoles(user));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.map(dbUser -> {
            dbUser.setEmail(user.getEmail());
            dbUser.setUsername(user.getUsername());
            if (user.isEnabled() == null) {
                dbUser.setEnabled(true);
            } else {
                dbUser.setEnabled(user.isEnabled());
            }
            dbUser.setRoles(getRoles(user));

            return Optional.of(userRepository.save(dbUser));
        }).orElseGet(Optional::empty);
    }

    @Override
    @Transactional
    public Optional<User> updatePassword(User user, String newPassword)
            throws PasswordDoNotMatchException {
        Long id = user.getId();
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User dbUser = userOptional.orElseThrow();
            String oldPassword = user.getPassword();
            String oldHashedPassword = dbUser.getPassword();

            if (passwordEncoder.matches(oldPassword, oldHashedPassword)) {
                dbUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                throw new PasswordDoNotMatchException("La actual contraseña es incorrecta");
            }
            return Optional.of(userRepository.save(dbUser));
        };

        return Optional.empty();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> addUserRoles(User user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.map(dbUser -> {
            dbUser.setRoles(user.getRoles());
            return Optional.of(userRepository.save(dbUser));
        }).orElseGet(Optional::empty);
    }

    @Override
    public Optional<User> blockUser(User user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.map(dbUser -> {
            dbUser.setEnabled(false);
            return Optional.of(userRepository.save(dbUser));
        }).orElseGet(Optional::empty);
    }

    private List<Role> getRoles(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");

        optionalRole.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> optionalAdminRole = roleRepository.findByName("ROLE_ADMIN");
            optionalAdminRole.ifPresent(roles::add);
        }

        return roles;
    }

}
