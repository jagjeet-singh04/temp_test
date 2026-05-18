package com.bnpp.assessment.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "wallets")
@SequenceGenerator(name = "wallet_seq" , sequenceName ="wallet_seq" , allocationSize = 1 )
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "cash_balance")
    private Double cashBalance;

    @Column(name = "used_margin")
    private Double usedMargin;

    @Column(name = "realised_pnl")
    private Double realisedPnl;

    @Column(name = "unrealised_pnl")
    private Double unrealisedPnl;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    // --- Constructors ---
    public Wallet() {
    }

    // --- Getters and Setters ---
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(Double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public Double getUsedMargin() {
        return usedMargin;
    }

    public void setUsedMargin(Double usedMargin) {
        this.usedMargin = usedMargin;
    }

    public Double getRealisedPnl() {
        return realisedPnl;
    }

    public void setRealisedPnl(Double realisedPnl) {
        this.realisedPnl = realisedPnl;
    }

    public Double getUnrealisedPnl() {
        return unrealisedPnl;
    }

    public void setUnrealisedPnl(Double unrealisedPnl) {
        this.unrealisedPnl = unrealisedPnl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}