package com.smart.serviceinterfaces;

import com.smart.domain.User;
import com.smart.exception.UserExistException;

import java.util.List;

public interface UserServiceInterface {
    public void register(User user) throws UserExistException;
    public void update(User user);
    public User getUserByUserName(String userName);
    public List<User> getAllUsers();
    public void loginSuccess(User user);
}
