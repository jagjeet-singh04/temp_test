package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Trade;
import com.bnpp.assessment.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TradeDaoImpl implements TradeDao {

    @Override
    public void save(Trade trade) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession() ; 
        try  {
            tx = session.beginTransaction();
            session.save(trade);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Trade> findByUserId(Long userId) {
    	Session session = HibernateUtil.getSessionFactory().openSession() ; 
        try  {
            return session.createQuery(
                    "FROM Trade t JOIN FETCH t.optionStrike WHERE t.user.userId = :uid")
                .setParameter("uid", userId)
                .list();
        }
        catch (Exception e) {
            
            throw new RuntimeException(e);
        }
        
    }
    
    @Override
    public List<Trade> findOpenTradesByUserId(Long userId) {
    	Session session = HibernateUtil.getSessionFactory().openSession() ; 
        
            return session.createQuery(
                    "FROM Trade t JOIN FETCH t.optionStrike WHERE t.user.id = :uid AND t.status = 'OPEN'"
                    )
                .setParameter("uid", userId)
                .list();
    }
    
    @Override
    public void update(Trade trade) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession() ; 
        try  {
            tx = session.beginTransaction();
            session.merge(trade);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
        finally {
        	session.close() ; 
        }
    }
    @Override
    public Trade findById(Long id) {
    	Session session = HibernateUtil.getSessionFactory().openSession() ; 
        
            return (Trade) session.get(Trade.class, id);
    }
}