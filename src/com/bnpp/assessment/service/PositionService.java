package com.bnpp.assessment.service;

import com.bnpp.assessment.dto.PositionRow;
import com.bnpp.assessment.models.Trade;
import java.util.List;

public interface PositionService {
    List<PositionRow> getPositions(Long userId);
}