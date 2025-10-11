package com.dao;

import java.util.List;
import com.entity.User;
import com.entity.Role;

public interface UserDAO {
    public List<User> getAllUsers();
    public User getUserById(Long id);
    public User getUserByUsername(String username);
    public User getUserByEmail(String email);
    public List<User> getUsersByRole(Role role);
    public boolean userExistsByUsername(String username);
    public boolean userExistsByEmail(String email);
    public boolean userExistsByUsernameAndPassword(String username, String password);
    public User addUser(User user);
    public List<User> addUserList(List<User> userList);
    public User updateUser(Long id, User user);
    public List<User> deleteUserById(Long id);
    public boolean deleteUserByUsername(String username);
    public User updateUserPassword(Long id, String newPassword);
    public User updateUserPasswordByUsername(String username, String newPassword);
}
