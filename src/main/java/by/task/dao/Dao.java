package by.task.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T entity);

    void delete(T entity);
}