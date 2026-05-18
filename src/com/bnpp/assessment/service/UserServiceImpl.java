package com.bnpp.assessment.service;

import javax.swing.JOptionPane;
import com.bnpp.assessment.dao.UserDao;
import com.bnpp.assessment.dao.UserDaoImpl;
import com.bnpp.assessment.dto.RegisterUserDto;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.models.Wallet; // Assuming standard models mapping structure

public class UserServiceImpl implements UserService {

    
    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public void register(RegisterUserDto user) {
        // TODO Auto-generated method stub
        
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(Long.parseLong(user.getPhone()));
        newUser.setAadhar(Long.parseLong(user.getAadhar()));
        newUser.setPan(user.getPan());

        Wallet wallet = new Wallet();
        wallet.setCashBalance(0.0);
        wallet.setUsedMargin(0.0);
        wallet.setRealisedPnl(0.0);
        wallet.setUnrealisedPnl(0.0);
        wallet.setUser(newUser);

        newUser.setWallet(wallet);

        userDao.registerUser(newUser);
      
    }

    @Override
    public Boolean checkUserCred(Long phone, String password) {
        // TODO Auto-generated method stub
        return userDao.checkUserCred(phone, password);
    }

    @Override
    public User findByUsername(String username) {
        // TODO Auto-generated method stub
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        // TODO Auto-generated method stub
        return userDao.findByEmail(email);
    }

    @Override
    public User findByPan(String pan) {
        // TODO Auto-generated method stub
        return userDao.findByPan(pan);
    }

    @Override
    public User findByPhone(Long phone) {
        // TODO Auto-generated method stub
        return userDao.findByPhone(phone);
    }

    @Override
    public User findByAadhar(Long aadhar) {
        // TODO Auto-generated method stub
        return userDao.findByAadhar(aadhar);
    }

    @Override
    public Boolean checkPassCorrectFromId(Long userId, String password) {
        // TODO Auto-generated method stub
        return userDao.checkPassCorrectFromId(userId, password);
    }

    @Override
    public User findByUserId(Long userId) {
        // TODO Auto-generated method stub
        return userDao.findByUserId(userId);
    }
}