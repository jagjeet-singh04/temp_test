package com.bnpp.assessment.service;

import com.bnpp.assessment.dao.UserDao;
import com.bnpp.assessment.dao.UserDaoImpl;
import com.bnpp.assessment.dao.WalletDao;
import com.bnpp.assessment.dao.WalletDaoImpl;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.models.Wallet;

public class WalletServiceImpl implements WalletService {

    private final WalletDao walletDao = new WalletDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public Wallet getWallet(Long userId) {
        Wallet wallet = walletDao.findByUserId(userId);
        // Auto-create wallet if missing (for new users)
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setCashBalance(0.0);
            wallet.setUsedMargin(0.0);
            wallet.setRealisedPnl(0.0);
            wallet.setUnrealisedPnl(0.0);
            User user = userDao.findByUserId(userId);
            wallet.setUser(user);
            walletDao.save(wallet);
        }
        return wallet;
    }

    @Override
    public boolean deposit(Long userId, double amount, String password) {
        User user = userDao.findByUserId(userId);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }

        Wallet wallet = getWallet(userId);  // ensures wallet exists
        wallet.setCashBalance(wallet.getCashBalance() + amount);
        walletDao.update(wallet);
        return true;
    }

    @Override
    public boolean withdraw(Long userId, double amount, String password) {
        User user = userDao.findByUserId(userId);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }

        Wallet wallet = getWallet(userId);
        if (wallet.getCashBalance() < amount) {
            return false;
        }

        wallet.setCashBalance(wallet.getCashBalance() - amount);
        walletDao.update(wallet);
        return true;
    }
}