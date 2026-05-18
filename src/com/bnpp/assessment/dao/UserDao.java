package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.User;

public interface UserDao {

    void registerUser(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByPan(String pan);

    User findByPhone(Long phone);

    Boolean checkUserCred(Long phone, String password);

    User findByAadhar(Long aadhar);

    Boolean checkPassCorrectFromId(Long userId, String password);

    User findByUserId(Long userId);
    
}