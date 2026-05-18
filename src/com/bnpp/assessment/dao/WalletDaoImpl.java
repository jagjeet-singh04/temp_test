package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Wallet;
import com.bnpp.assessment.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WalletDaoImpl implements WalletDao {

    @Override
    public Wallet findByUserId(Long userId) {
    	Session session = HibernateUtil.getSessionFactory().openSession() ;
 
            return (Wallet) session.createQuery(
                    "FROM Wallet w JOIN FETCH w.user WHERE w.user.userId = :uid")
                .setParameter("uid", userId)
                .uniqueResult();
        
    }

    @Override
    public void update(Wallet wallet) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession() ;
        try {
            tx = session.beginTransaction();
            session.merge(wallet);   // merges the detached wallet
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
        finally {
        	session.close() ;
        }
    }

    // New method: create a wallet for a new user
    @Override
    public void save(Wallet wallet) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession() ;
        try  {
            tx = session.beginTransaction();
            session.save(wallet);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
        finally {
        	session.close() ; 
        }
    }
}