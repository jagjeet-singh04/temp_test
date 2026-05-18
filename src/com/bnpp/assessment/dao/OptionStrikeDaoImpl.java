package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.OptionChain;
import com.bnpp.assessment.models.OptionStrike;
import com.bnpp.assessment.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OptionStrikeDaoImpl
        implements OptionStrikeDao {

    // =========================
    // FIND STRIKES BY INDEX
    // =========================

    @Override
    public List<OptionStrike>
    findByIndexId(Long indexId) {

        Session session =
                HibernateUtil
                        .getSessionFactory()
                        .openSession();

        List<OptionStrike> list =
                session.createQuery(
                        "FROM OptionStrike " +
                        "WHERE optionChain.index.id = :id"
                       
                )
                .setParameter("id", indexId)
                .list();

        session.close();

        return list;
    }

    // =========================
    // UPDATE STRIKE
    // =========================

    @Override
    public void update(
            OptionStrike strike
    ) {

        Session session =
                HibernateUtil
                        .getSessionFactory()
                        .openSession();

        Transaction transaction =
                session.beginTransaction();

        session.merge(strike);

        transaction.commit();

        session.close();
    }

    // =========================
    // FIND ALL STRIKES
    // =========================

    @Override
    public List<OptionStrike> findAll() {

        Session session =
                HibernateUtil
                        .getSessionFactory()
                        .openSession();

        List<OptionStrike> strikes =
                session.createQuery(
                        "FROM OptionStrike"
                    
                ).list();

        session.close();

        return strikes;
    }
    
    @Override
    public void updateChain(
            OptionChain chain
    ) {

        Session session =
                HibernateUtil
                        .getSessionFactory()
                        .openSession();

        Transaction tx =
                session.beginTransaction();

        session.merge(chain);

        tx.commit();

        session.close();
    }
    
 

    // OptionStrikeDaoImpl
    @Override
    public OptionStrike findById(Long id) {
    	Session session = HibernateUtil.getSessionFactory().openSession() ; 
    	
            return (OptionStrike) session.get(OptionStrike.class, id);
        
    }
}