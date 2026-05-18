package com.bnpp.assessment.service;

import com.bnpp.assessment.models.Wallet;

public interface WalletService {

    Wallet getWallet(Long userId);

    boolean deposit(
            Long userId,
            double amount,
            String password
    );

    boolean withdraw(
            Long userId,
            double amount,
            String password
    );
}