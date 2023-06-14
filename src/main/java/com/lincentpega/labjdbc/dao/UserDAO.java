package com.lincentpega.labjdbc.dao;

import com.lincentpega.labjdbc.model.User;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAO {
    public static final String urlBase = "jdbc:postgresql://localhost:5432/";
    public static final String username = "postgres";
    public static final String password = "postgres";
    private final ApplicationContext context;
    private DataSource dataSource;


    public UserDAO(ApplicationContext context, DataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void createDatabase(String dbName) {
        String createDBStmt = "CREATE DATABASE ";
        try (Connection connection = context.getBean(DataSource.class).getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createDBStmt + dbName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.dataSource = new DriverManagerDataSource(urlBase + dbName, username, password);
        try (Connection connection = dataSource.getConnection()) {
            String initScript = DBUtils.readInitScript();
            connection.createStatement().execute(initScript);
        } catch (SQLException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDatabase(String dbName) {
        if (checkDatabaseExists(dbName)) {
            dataSource = new DriverManagerDataSource(urlBase + dbName, username, password);
        }
    }

    public boolean checkDatabaseExists(String dbName) {
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall("SELECT check_database_exists(?)")) {
                statement.setString(1, dbName);
                ResultSet resultSet = statement.executeQuery();

                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<User> findAllUsers(String nameFilter) {
        if (nameFilter == null || nameFilter.isEmpty()) {
            return getAll();
        } else {
            return findByName(nameFilter);
        }
    }

    private List<User> getAll() {
        String functionCall = "SELECT id, name, age FROM get_all_users()";
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall(functionCall)) {
                ResultSet resultSet = statement.executeQuery();
                ArrayList<User> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(new User(resultSet.getLong("id"), resultSet.getString("name"),
                            resultSet.getLong("age")));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void createUser(User user)  {
        String functionCall = "SELECT insert_user(?, ?, ?)";
        executeUserStmt(user, functionCall);
    }

    @SneakyThrows
    public void deleteUser(Long id) {
        String functionCall = "SELECT delete_user(?)";
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall(functionCall)) {
                statement.setLong(1, id);

                statement.execute();
            }
        }
    }

    public boolean checkUserExists(Long id) {
        String functionCall = "SELECT * FROM check_user_exists(?)";
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall(functionCall)) {
                statement.setLong(1, id);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                } else {
                    throw new SQLException("Something went wrong");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(User user) {
        String procedureCall = "CALL update_user(?, ?, ?)";
        executeUserStmt(user, procedureCall);
    }

    public List<User> findByName(String name) {
        String query = "SELECT id, name, age FROM find_user_by_name(?)";
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement stmt = connection.prepareCall(query)) {
                stmt.setString(1, name);
                ResultSet resultSet = stmt.executeQuery();

                ArrayList<User> users = new ArrayList<>();
                while (resultSet.next()) {
                    User user = new User(resultSet.getLong("id"), resultSet.getString("name"),
                            resultSet.getLong("age"));
                    users.add(user);
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearTable() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement call = connection.prepareCall("SELECT clear_user_table()")) {
                call.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDatabase(String dbName) {
        String deleteDBStmt = "DROP DATABASE ";

        try (Connection connection = context.getBean(DataSource.class).getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(deleteDBStmt + dbName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.dataSource = context.getBean(DataSource.class);
    }

    private void executeUserStmt(User user, String procedureCall) {
        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement statement = connection.prepareCall(procedureCall)) {
                statement.setLong(1, user.getId());
                statement.setString(2, user.getName());
                statement.setLong(3, user.getAge());

                statement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
