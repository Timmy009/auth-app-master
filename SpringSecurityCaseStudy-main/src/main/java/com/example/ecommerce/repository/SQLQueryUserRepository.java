package com.example.ecommerce.repository;


import com.example.ecommerce.model.SQLQueryUserRequest;
import com.example.ecommerce.model.SqlQueryUser;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.sql.SQLException;
import java.util.Formatter;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
@RequiredArgsConstructor
public class SQLQueryUserRepository {

        private ModelMapper mapper = new ModelMapper();

        public SqlQueryUser save(SQLQueryUserRequest sqlQueryUserRequest) throws SQLException {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/sql_db?createDatabaseIfNotExist=true",
                    "root",
                    "nedstark");
            String sqlTableCreationQuery = """
                                    CREATE TABLE if not exists sql_db_table_1(
                                    id int not null auto_increment,
                                    first_name varchar(255) null,
                                    last_name varchar(255) null,
                                    username varchar(15) null,
                                    password varchar(15) null,
                                    primary key (id)
                                    );
                                    """;
            PreparedStatement tableCreationStatement = connection.prepareStatement(sqlTableCreationQuery);
            tableCreationStatement.executeUpdate();

            String sqlInsertionQuery = """
                                       insert into sql_db_table_1 (id, first_name, last_name, username, password) values (null, ?, ?, ?, ?)
                                       """;
            PreparedStatement insertionStatement = connection.prepareStatement(sqlInsertionQuery, RETURN_GENERATED_KEYS);
            insertionStatement.setString(1, sqlQueryUserRequest.getFirstName());
            insertionStatement.setString(2, sqlQueryUserRequest.getLastName());
            insertionStatement.setString(3, sqlQueryUserRequest.getUsername());
            insertionStatement.setString(4, sqlQueryUserRequest.getPassword());
            insertionStatement.executeUpdate();

            ResultSet keys = insertionStatement.getGeneratedKeys();

            SqlQueryUser savedUser = null;
            if (keys.next()) {
                long fetchedKey = keys.getLong(1);
                String sqlRetrievalQuery = "select * from sql_db_table_1 where id = %d";
                String formattedString = String.format(sqlRetrievalQuery, fetchedKey);
                PreparedStatement retrievalStatement = connection.prepareStatement(formattedString);
                ResultSet resultSet = retrievalStatement.executeQuery();
                if (resultSet.next()) {
                    sqlQueryUserRequest.setId(resultSet.getLong(1));
                    sqlQueryUserRequest.setFirstName(resultSet.getString(2));
                    sqlQueryUserRequest.setLastName(resultSet.getString(3));
                    sqlQueryUserRequest.setUsername(resultSet.getString(4));
                    sqlQueryUserRequest.setPassword(resultSet.getString(5));
                }
                savedUser = mapper.map(sqlQueryUserRequest, SqlQueryUser.class);
            }

            return savedUser;
        }

        public void insert( String username, boolean is_locked, Connection connection) throws SQLException {
            String sqlInsertionQuery = """
                                   insert into diaries (id, ${username}, ${is_locked}) values (null, ?, ?)
                                   """;
            PreparedStatement insertionStatement = connection.prepareStatement(sqlInsertionQuery, RETURN_GENERATED_KEYS);
//            insertionStatement.setString(1, username);
//            insertionStatement.setBoolean(2, true);
            insertionStatement.executeUpdate();
        }


    public static void main(String[] args) {
        SQLQueryUserRequest sqlQueryUserRequest = new SQLQueryUserRequest();
        sqlQueryUserRequest.setPassword("password");
        sqlQueryUserRequest.setUsername("username");
        sqlQueryUserRequest.setFirstName("firstName");
        sqlQueryUserRequest.setLastName("lastName");

        SQLQueryUserRepository sqlQueryUserRepository = new SQLQueryUserRepository();

        try {
            System.out.println(sqlQueryUserRepository.save(sqlQueryUserRequest));
            System.out.println(sqlQueryUserRepository.save(sqlQueryUserRequest));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
