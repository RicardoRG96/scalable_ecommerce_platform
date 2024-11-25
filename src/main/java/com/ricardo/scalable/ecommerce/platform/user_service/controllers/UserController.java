package com.ricardo.scalable.ecommerce.platform.user_service.controllers;

import com.ricardo.scalable.ecommerce.platform.user_service.entities.User;
import com.ricardo.scalable.ecommerce.platform.user_service.exceptions.PasswordDoNotMatchException;
import com.ricardo.scalable.ecommerce.platform.user_service.repositories.dto.UserUpdateInfoDto;
import com.ricardo.scalable.ecommerce.platform.user_service.repositories.dto.UserUpdatePasswordDto;
import com.ricardo.scalable.ecommerce.platform.user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        user.setAdmin(false);
        return this.createUser(user, result);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors()
                .forEach(err -> {
                    errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
                });
        return ResponseEntity.badRequest().body(errors);
    }

    // revisar validacion del campo de password, ver como lo puedo ignorar en este endpoint
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody UserUpdateInfoDto userUpdated,
            @PathVariable Long id,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return this.validation(result);
        }

        Optional<User> userOptional = userService.update(userUpdated, id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody UserUpdatePasswordDto userUpdated,
            @PathVariable Long id,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            this.validation(result);
        }
        try {
            Optional<User> userOptional = userService.updatePassword(userUpdated, id);
            if (userOptional.isPresent()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (PasswordDoNotMatchException ex) {
            Map<String, String> responseNotMatchedPassword = new HashMap<>();
            responseNotMatchedPassword.put("message", ex.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseNotMatchedPassword);
        }
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<User> changeUserRoles(@RequestBody User user, @PathVariable Long id) {
        Optional<User> userOptional = userService.addUserRoles(user, id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<User> blockUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.blockUser(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/unlock/{id}")
    public ResponseEntity<User> unlockUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.unlockUser(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
