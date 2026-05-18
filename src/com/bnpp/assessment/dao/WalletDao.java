package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Wallet;

public interface WalletDao {

    Wallet findByUserId(Long userId);

    void update(Wallet wallet);
    void save(Wallet wallet);
}