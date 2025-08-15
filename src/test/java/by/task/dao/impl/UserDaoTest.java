package by.task.dao.impl;

import by.task.dao.Dao;
import by.task.models.User;
import by.task.util.TestHibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
class UserDaoTest {
    private static final String TEST_NAME = "Test User";
    private static final String ALT_NAME = "Alt User";
    private static final String TEST_EMAIL = "user@test.com";
    private static final String ALT_EMAIL = "alt@test.com";
    private static final int TEST_AGE = 30;
    private static final int ALT_AGE = 20;

    private SessionFactory sessionFactory;
    private Dao<User, Long> userDao;

    @BeforeAll
    void setup() {
        sessionFactory = TestHibernateUtil.buildSessionFactory();
        userDao = new UserDao(sessionFactory);
    }

    @AfterAll
    void tearDown() {
        sessionFactory.close();
        TestHibernateUtil.shutdown();
    }

    @BeforeEach
    void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void shouldSaveUserAndRetrieveItById_WhenUserIsValid() {
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);

        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals(TEST_NAME, found.get().getName());
        assertEquals(TEST_EMAIL, found.get().getEmail());
        assertEquals(TEST_AGE, found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    void shouldReturnAllUsers_WhenMultipleUsersExist() {
        User user1 = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        User user2 = new User(ALT_NAME, ALT_EMAIL, ALT_AGE);

        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void shouldUpdateUserDetails_WhenNewValuesAreValid() {
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        userDao.save(user);

        user.setName(ALT_NAME);
        user.setEmail(ALT_EMAIL);
        user.setAge(ALT_AGE);

        userDao.update(user);

        Optional<User> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(ALT_NAME, updated.get().getName());
        assertEquals(ALT_EMAIL, updated.get().getEmail());
        assertEquals(ALT_AGE, updated.get().getAge());
    }

    @Test
    void shouldDeleteUser_WhenUserExists() {
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        userDao.save(user);

        assertTrue(userDao.findById(user.getId()).isPresent());
        userDao.delete(user);

        Optional<User> deleted = userDao.findById(user.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldReturnEmptyOptional_WhenUserNotFoundById() {
        Optional<User> result = userDao.findById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSetCreatedAtAutomatically_WhenNewUserIsSaved() {
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);

        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertNotNull(found.get().getCreatedAt());
        assertTrue(found.get().getCreatedAt().isAfter(beforeTest));
    }
}