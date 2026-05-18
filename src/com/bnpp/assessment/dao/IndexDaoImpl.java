package com.bnpp.assessment.dao;

import com.bnpp.assessment.models.Index;
import com.bnpp.assessment.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class IndexDaoImpl implements IndexDao {

    @Override
    public List<Index> findAll() {

    	Session session = HibernateUtil.getSessionFactory().openSession(); 
        
            return session.createQuery("FROM Index").list();
        
    }

    @Override
    public Index findById(Long id) {
    	Session session = HibernateUtil.getSessionFactory().openSession(); 
            return (Index) session.get(Index.class, id);
        
    }

    @Override
    public void save(Index index) {
        Transaction tx = null;

    	Session session = HibernateUtil.getSessionFactory().openSession(); 
        try  {
            tx = session.beginTransaction();
            session.save(index);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(Index index) {
        Transaction tx = null;

    	Session session = HibernateUtil.getSessionFactory().openSession(); 
        try  {
            tx = session.beginTransaction();
            session.merge(index);          // merge updates an existing entity
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}