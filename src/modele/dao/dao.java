package modele.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public interface dao<T> {
    void setConnection(Connection cn);

    void create(T obj) throws SQLException;

    void update(T obj) throws SQLException;

    void delete(T obj) throws SQLException;

    T findById(String... id) throws SQLException;

    Collection<T> findAll() throws SQLException;
}