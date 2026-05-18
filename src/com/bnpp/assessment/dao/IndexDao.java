package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Index;
import java.util.List;

public interface IndexDao {
    List<Index> findAll();
    Index findById(Long id);
    void save(Index index);
    void update(Index index);   // <-- add this
}