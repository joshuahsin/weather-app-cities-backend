package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.UserDAO;
import com.entity.User;
import com.entity.Role;
import com.exception.UserNotFoundException;
import com.repository.UserRepository;

@Service
public class UserDaoImpl implements UserDAO {
    @Autowired
    private UserRepository userRepo;
    
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepo.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException(id);
    }
    
    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if(user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException(username);
    }
    
    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if(user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException("Could not find user with email " + email);
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
        return userRepo.existsByUsernameAndPassword(username, password);
    }

    @Override
    public User addUser(User user) {
        return userRepo.save(user);
    }
    
    @Override
    public List<User> addUserList(List<User> userList) {
        return userRepo.saveAll(userList);
    }

    @Override
    public User updateUser(Long id, User user) {
        if(userRepo.findById(id).isPresent()) {
            user.setId(id); // Ensure the ID is set for update
            return userRepo.save(user);
        }
        throw new UserNotFoundException(id);
    }

    @Override
    public List<User> deleteUserById(Long id) {
        if(userRepo.findById(id).isPresent()) {
            userRepo.deleteById(id);
            return userRepo.findAll();
        }
        throw new UserNotFoundException(id);
    }

    @Override
    public boolean deleteUserByUsername(String username) {
        long result = userRepo.deleteByUsername(username);
        if(result == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    @Transactional
    public User updateUserPassword(Long id, String newPassword) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            return userRepo.save(user);
        }
        throw new UserNotFoundException(id);
    }

    @Override
    @Transactional
    public User updateUserPasswordByUsername(String username, String newPassword) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            return userRepo.save(user);
        }
        throw new UserNotFoundException(username);
    }
}
