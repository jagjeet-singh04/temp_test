package com.bnpp.assessment.service;

import com.bnpp.assessment.models.OptionStrike;

import java.util.List;

public interface MarketSimulationService {

    List<OptionStrike> getStrikesByIndex(
            Long indexId
    );

    void simulateMarketMove(
            Long indexId,
            double move
    );
}