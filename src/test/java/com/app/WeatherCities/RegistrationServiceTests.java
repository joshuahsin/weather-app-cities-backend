package com.app.WeatherCities;

import com.entity.PendingUser;
import com.entity.Role;
import com.entity.User;
import com.repository.PendingUserRepository;
import com.repository.UserRepository;
import com.service.EmailService;
import com.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {

    @Mock private PendingUserRepository pendingUserRepo;
    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private RegistrationService registrationService;

    // --- initiateRegistration ---

    @Test
    void testInitiateRegistration() {
        User user = new User("john", "john@example.com", "password", Role.USER);
        when(userRepo.existsByUsername("john")).thenReturn(false);
        when(userRepo.existsByEmail("john@example.com")).thenReturn(false);
        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$hash");
        when(pendingUserRepo.save(any(PendingUser.class))).thenAnswer(i -> i.getArgument(0));

        String result = registrationService.initiateRegistration(user);

        assertThat(result).contains("john@example.com");

        ArgumentCaptor<PendingUser> captor = ArgumentCaptor.forClass(PendingUser.class);
        verify(pendingUserRepo).save(captor.capture());
        PendingUser saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("$2a$10$hash");
        assertThat(saved.getVerificationCode()).hasSize(6).containsOnlyDigits();
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now());

        verify(emailService).sendVerificationCode(eq("john@example.com"), eq("john"), anyString());
    }

    @Test
    void testInitiateRegistrationUsernameTaken() {
        User user = new User("john", "john@example.com", "password", Role.USER);
        when(userRepo.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.initiateRegistration(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username already taken");

        verify(pendingUserRepo, never()).save(any());
        verify(emailService, never()).sendVerificationCode(any(), any(), any());
    }

    @Test
    void testInitiateRegistrationEmailTaken() {
        User user = new User("john", "john@example.com", "password", Role.USER);
        when(userRepo.existsByUsername("john")).thenReturn(false);
        when(userRepo.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.initiateRegistration(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email already in use");

        verify(pendingUserRepo, never()).save(any());
        verify(emailService, never()).sendVerificationCode(any(), any(), any());
    }

    @Test
    void testInitiateRegistrationOverwritesExistingPending() {
        User user = new User("john", "john@example.com", "password", Role.USER);
        PendingUser existing = new PendingUser();
        existing.setEmail("john@example.com");
        existing.setVerificationCode("000000");

        when(userRepo.existsByUsername("john")).thenReturn(false);
        when(userRepo.existsByEmail("john@example.com")).thenReturn(false);
        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$hash");
        when(pendingUserRepo.save(any(PendingUser.class))).thenAnswer(i -> i.getArgument(0));

        registrationService.initiateRegistration(user);

        // existing object is updated (same reference), not a new one
        verify(pendingUserRepo).save(same(existing));
        // a new code was generated (different from the old one is not guaranteed, but it is set)
        assertThat(existing.getVerificationCode()).isNotNull().hasSize(6);
        verify(emailService).sendVerificationCode(eq("john@example.com"), eq("john"), anyString());
    }

    // --- completeRegistration ---

    @Test
    void testCompleteRegistration() {
        PendingUser pending = pendingUser("123456", LocalDateTime.now().plusMinutes(10));
        User saved = new User("john", "john@example.com", "$2a$10$hash", Role.USER);

        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.of(pending));
        when(userRepo.save(any(User.class))).thenReturn(saved);

        User result = registrationService.completeRegistration("john@example.com", "123456");

        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepo).save(any(User.class));
        verify(pendingUserRepo).delete(pending);
    }

    @Test
    void testCompleteRegistrationPendingNotFound() {
        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.completeRegistration("john@example.com", "123456"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No pending registration");

        verify(userRepo, never()).save(any());
    }

    @Test
    void testCompleteRegistrationExpiredCode() {
        PendingUser pending = pendingUser("123456", LocalDateTime.now().minusMinutes(1));
        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> registrationService.completeRegistration("john@example.com", "123456"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("expired");

        verify(pendingUserRepo).delete(pending); // stale entry cleaned up
        verify(userRepo, never()).save(any());
    }

    @Test
    void testCompleteRegistrationWrongCode() {
        PendingUser pending = pendingUser("123456", LocalDateTime.now().plusMinutes(10));
        when(pendingUserRepo.findByEmail("john@example.com")).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> registrationService.completeRegistration("john@example.com", "999999"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid verification code");

        verify(userRepo, never()).save(any());
        verify(pendingUserRepo, never()).delete(any());
    }

    // --- helper ---

    private PendingUser pendingUser(String code, LocalDateTime expiry) {
        PendingUser p = new PendingUser();
        p.setUsername("john");
        p.setEmail("john@example.com");
        p.setPassword("$2a$10$hash");
        p.setRole(Role.USER);
        p.setVerificationCode(code);
        p.setExpiresAt(expiry);
        return p;
    }
}
