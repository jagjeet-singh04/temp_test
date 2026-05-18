package com.bnpp.assessment.service;

import com.bnpp.assessment.dto.RegisterUserDto;
import com.bnpp.assessment.models.User;

public interface UserService {

    void register(RegisterUserDto user);

    Boolean checkUserCred(Long phone, String password);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByPan(String pan);

    User findByPhone(Long phone);

    User findByAadhar(Long aadhar);

    User findByUserId(Long userId);

    Boolean checkPassCorrectFromId(Long userId, String password);
    
}