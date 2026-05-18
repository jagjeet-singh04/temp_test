package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.OptionChain;
import com.bnpp.assessment.models.OptionStrike;

import java.util.List;

public interface OptionStrikeDao {

    List<OptionStrike> findByIndexId(Long indexId);

	List<OptionStrike> findAll();

	void update(OptionStrike strike);
	
	void updateChain(OptionChain chain);
	// OptionStrikeDao interface
    OptionStrike findById(Long id);
}