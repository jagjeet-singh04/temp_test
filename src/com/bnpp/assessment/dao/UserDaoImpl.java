package com.bnpp.assessment.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.util.HibernateUtil; // Assuming standard utility package structure

public class UserDaoImpl implements UserDao {

   

    @Override
    public void registerUser(User user) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            
        }
        catch (Exception e) {
            if (tx != null) tx.rollback();
            
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByUsername(String username) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            Query findByUsernameQuery = session.createQuery("From User u where u.username = :username");
            findByUsernameQuery.setParameter("username", username);
            List<User> user = findByUsernameQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
           
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            Query findByEmailQuery = session.createQuery("From User u where u.email = :email");
            findByEmailQuery.setParameter("email", email);
            List<User> user = findByEmailQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
          
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByPan(String pan) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            Query findByPanQuery = session.createQuery("From User u where u.pan = :pan");
            findByPanQuery.setParameter("pan", pan);
            List<User> user = findByPanQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
          
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByPhone(Long phone) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            Query findByPhoneQuery = session.createQuery("From User u where u.phone = :phone");
            findByPhoneQuery.setParameter("phone", phone);
            List<User> user = findByPhoneQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
         
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Boolean checkUserCred(Long phone, String password) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        Boolean correct = false;
        try {
            Query check = session.createQuery("From User u where u.phone = :phone and u.password = :password");
            check.setParameter("phone", phone);
            check.setParameter("password", password);
            List<User> user = check.list();
            if (user != null && !user.isEmpty()) {
                correct = true;
            }
            return correct;
        }
        catch (Exception e) {
          
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByAadhar(Long aadhar) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            Query findByAadharQuery = session.createQuery("From User u where u.aadhar = :aadhar");
            findByAadharQuery.setParameter("aadhar", aadhar);
            List<User> user = findByAadharQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Boolean checkPassCorrectFromId(Long userId, String password) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        Boolean correct = false;
        try {
            Query check = session.createQuery("From User u where u.userId = :userId and u.password = :password");
            check.setParameter("userId", userId);
            check.setParameter("password", password);
            List<User> user = check.list();
            if (user != null && !user.isEmpty()) {
                correct = true;
            }
            return correct;
        }
        catch (Exception e) {
            
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public User findByUserId(Long userId) {
        // TODO Auto-generated method stub
        Session session = HibernateUtil.getSessionFactory().openSession();
        User getUser = null;
        try {
            // Note: The original image code has a typo line 247 where query instance assigns to 'findByAadharQuery' 
            // instead of a distinctive name, but functionality follows standard assignment rules.
            Query findByUserIdQuery = session.createQuery("From User u where u.userId = :userId");
            findByUserIdQuery.setParameter("userId", userId);
            List<User> user = findByUserIdQuery.list();
            for (User u : user) {
                getUser = u;
            }
            return getUser;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }
}