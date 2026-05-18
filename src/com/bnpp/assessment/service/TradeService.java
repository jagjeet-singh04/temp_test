package com.bnpp.assessment.service;

import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.models.User;

public interface TradeService {
    boolean executeTrade(User user, OptionStrike strike, String optionType, String tradeType, int quantity);
    boolean closeTrade(Long tradeId, User user);
}