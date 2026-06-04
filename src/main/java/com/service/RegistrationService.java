package com.service;

import com.entity.PendingUser;
import com.entity.Role;
import com.entity.User;
import com.repository.PendingUserRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private static final int EXPIRY_MINUTES = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private PendingUserRepository pendingUserRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String initiateRegistration(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);

        // Upsert: overwrite any existing pending entry for this email (re-send flow)
        PendingUser pending = pendingUserRepo.findByEmail(user.getEmail())
                .orElse(new PendingUser());
        pending.setUsername(user.getUsername());
        pending.setEmail(user.getEmail());
        pending.setPassword(hashedPassword);
        pending.setRole(user.getRole() != null ? user.getRole() : Role.USER);
        pending.setVerificationCode(code);
        pending.setExpiresAt(expiry);
        pendingUserRepo.save(pending);

        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);
        return "Verification code sent to " + user.getEmail();
    }

    public User completeRegistration(String email, String code) {
        PendingUser pending = pendingUserRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No pending registration found for this email"));

        if (LocalDateTime.now().isAfter(pending.getExpiresAt())) {
            pendingUserRepo.delete(pending);
            throw new ResponseStatusException(HttpStatus.GONE,
                    "Verification code has expired — please register again");
        }

        if (!pending.getVerificationCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code");
        }

        User newUser = new User(
                pending.getUsername(),
                pending.getEmail(),
                pending.getPassword(), // already hashed
                pending.getRole()
        );
        User saved = userRepo.save(newUser);
        pendingUserRepo.delete(pending);
        return saved;
    }
}
