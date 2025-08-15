package by.task.dao.impl;

import by.task.dao.Dao;
import by.task.exceptions.dao.UserPersistenceException;
import by.task.exceptions.dao.UserQueryException;
import by.task.models.User;
import by.task.util.HibernateUtil;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserDao implements Dao<User, Long> {
    private final SessionFactory sessionFactory;

    public UserDao() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new UserPersistenceException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        } catch (Exception e) {
            throw new UserQueryException("Ошибка при поиске пользователя по ID: " + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
            cq.from(User.class);
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new UserQueryException("Ошибка при получении списка пользователей", e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new UserPersistenceException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new UserPersistenceException("Ошибка при удалении пользователя", e);
        }
    }
}