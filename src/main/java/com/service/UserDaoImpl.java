package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.UserDAO;
import com.entity.Role;
import com.entity.User;
import com.exception.UserNotFoundException;
import com.repository.UserRepository;

@Service
public class UserDaoImpl implements UserDAO {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with email " + email));
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepo.findByRole(role);
    }

    @Override
    public boolean userExistsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public boolean userExistsByUsernameAndPassword(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public List<User> addUserList(List<User> userList) {
        for (User user : userList) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepo.saveAll(userList);
    }

    @Override
    public User updateUser(Long id, User update) {
        User existing = userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        existing.setUsername(update.getUsername());
        existing.setEmail(update.getEmail());
        existing.setRole(update.getRole());
        return userRepo.save(existing);
    }

    @Override
    public List<User> deleteUserById(Long id) {
        if (userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return userRepo.findAll();
        }
        throw new UserNotFoundException(id);
    }

    @Override
    public boolean deleteUserByUsername(String username) {
        return userRepo.deleteByUsername(username) > 0;
    }

    @Override
    @Transactional
    public User updateUserPassword(Long id, String newPassword) {
        User user = userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public User updateUserPasswordByUsername(String username, String newPassword) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepo.save(user);
    }
}
