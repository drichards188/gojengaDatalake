package com.hyperion.datalake.handlers;

import com.hyperion.datalake.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Scenario 1: Failover happens when autocommit is set to true - Catch SQLException with code 08S02.
 */
public class UserHandler {

    private static final String CONNECTION_STRING = "jdbc:mysql:aws://gojenga-mysql.ctmemwqudyt5.us-east-2.rds.amazonaws.com:3306/gojenga";
    private static final String USERNAME = "admin";

    private static final String PASSWORD = System.getenv("GOJENGA_DB_PWD");
    private static final int MAX_RETRIES = 5;

    public static void getAllFromUsersTest() throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            // Configure the connection.
            setInitialSessionState(conn);

            // Do something with method "betterExecuteQuery" using the Cluster-Aware Driver.
            String select_sql = "SELECT * FROM usersTest";
            try (ResultSet rs = betterExecuteQuery(conn, select_sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("name"));
                }
            }
        }
    }

    public static HashMap<String, HashMap<String,String>> readData(String columns, String table, String selector) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            // Configure the connection.
            setInitialSessionState(conn);
            HashMap<String, HashMap<String, String>> result = new HashMap<>();

            // Do something with method "betterExecuteQuery" using the Cluster-Aware Driver.
            String select_sql = String.format("SELECT %s FROM %s WHERE %s;", columns, table, selector);
            try (ResultSet rs = betterExecuteQuery(conn, select_sql)) {
                while (rs.next()) {
                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    int count = rsMetaData.getColumnCount();
                    String name = rs.getString("name");

                    HashMap<String, String> userMap = new HashMap<>();

                    for (int i = 1; i <= count; i++) {
                        String column = rsMetaData.getColumnName(i);
                        String value = rs.getString(column);
                        userMap.put(column, value);
                        System.out.println(column + ": " + value);
                    }
                    result.put(name, userMap);
                }
            }

            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
    }

    public static HashMap<String, String> createData(String table, String statement) {
        HashMap<String, String> response = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            // Configure the connection.
            setInitialSessionState(conn);

            String insert_sql = String.format("INSERT INTO %s VALUES %s", table, statement);

            try (Statement stmt = conn.createStatement()) {
                int insertResponse = stmt.executeUpdate(insert_sql);
                if (insertResponse > 0) {
                    response.put("success", "user created");
                } else {
                    response.put("error", "user already exists");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                response.put("error", e.getMessage());
                return response;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            response.put("error", e.getMessage());
            return response;
        }
        return response;
    }

    public static boolean updateData(String table, String statement, String selector) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            // Configure the connection.
            setInitialSessionState(conn);

            String update_sql = String.format("UPDATE %s SET %s WHERE %s;", table, statement, selector);

            try (Statement stmt = conn.createStatement()) {
                int updateResponse = stmt.executeUpdate(update_sql);
                if (updateResponse > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean deleteData(String table, String selector) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            // Configure the connection.
            setInitialSessionState(conn);

            String delete_sql = String.format("DELETE FROM %s WHERE %s;", table, selector);

            try (Statement stmt = conn.createStatement()) {
                int deleteResponse = stmt.executeUpdate(delete_sql);
                if (deleteResponse > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void setInitialSessionState(Connection conn) throws SQLException {
        // Your code here for the initial connection setup.
        try (Statement stmt1 = conn.createStatement()) {
            stmt1.executeUpdate("SET time_zone = \"+00:00\"");
        }
    }

    // A better executing query method when autocommit is set as the default value - true.
    private static ResultSet betterExecuteQuery(Connection conn, String query) throws SQLException {
        // Record the times of retry.
        int retries = 0;

        while (true) {
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery(query);
            } catch (SQLException e) {
                // If the attempt to connect has failed MAX_RETRIES times,
                // throw the exception to inform users of the failed connection.
                if (retries > MAX_RETRIES) {
                    throw e;
                }

                // Failover has occurred and the driver has failed over to another instance successfully.
                if ("08S02".equalsIgnoreCase(e.getSQLState())) {
                    // Reconfigure the connection.
                    setInitialSessionState(conn);
                    // Re-execute that query again.
                    retries++;

                } else {
                    // If some other exception occurs, throw the exception.
                    throw e;
                }
            }
        }
    }
}