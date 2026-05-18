package com.bnpp.assessment.models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "trades")
@SequenceGenerator(name = "trade_seq", sequenceName = "trade_sequence", allocationSize = 1)

public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trade_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "option_strike_id")
    private OptionStrike optionStrike;

    @Column(name = "option_type")
    private String optionType;

    @Column(name = "trade_type")
    private String tradeType;

    private Integer quantity;

    private Double premium;

    @Column(name = "total_amount")
    private Double totalAmount;
    
 // inside Trade.java
    @Column(name = "status")
    private String status;   // "OPEN" or "CLOSED"

   

    @Column(name = "trade_time")
    @Temporal(TemporalType.TIMESTAMP) // Ensures both date and time precision details persist 
    private Date tradeTime;

    // --- Constructors ---
    public Trade() {}
    

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // getter & setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public OptionStrike getOptionStrike() { return optionStrike; }
    public void setOptionStrike(OptionStrike optionStrike) { this.optionStrike = optionStrike; }

    public String getOptionType() { return optionType; }
    public void setOptionType(String optionType) { this.optionType = optionType; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPremium() { return premium; }
    public void setPremium(Double premium) { this.premium = premium; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Date getTradeTime() { return tradeTime; }
    public void setTradeTime(Date tradeTime) { this.tradeTime = tradeTime; }
}