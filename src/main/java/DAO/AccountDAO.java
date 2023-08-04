package DAO;

import Model.Account;
import java.sql.*;
import Util.ConnectionUtil;
import Util.UncheckedSQLException;


public class AccountDAO {
    private static final AccountDAO INSTANCE = new AccountDAO();

    private AccountDAO() {}

    public static AccountDAO getInstance() {
        return INSTANCE;
    }
    
    public boolean accountWithIdExists(int id) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "SELECT EXISTS (SELECT 1 FROM account WHERE account_id = ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                } else {
                    throw new SQLException("Something went wrong");
                }
            }
        }catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public boolean accountWithUsernameExists(String username) {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "SELECT EXISTS (SELECT 1 FROM account WHERE username = ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                } else {
                    throw new SQLException("Something went wrong");
                }
            }
        }catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public Account findAccount(Account account) {
        String sql = "SELECT * FROM account where username = ? AND password = ?";
        try (PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(rs.getInt(1), rs.getString(2), rs.getString(3));
                } else {
                    return null;
                }
            }
        }catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public Account insertAccount(Account account) {
        String sql = "INSERT INTO account(username, password) VALUES(?, ?)";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();
            try(ResultSet pkeyResultSet = ps.getGeneratedKeys()) {
                if(pkeyResultSet.next()){
                    int generatedAccountId = (int) pkeyResultSet.getLong(1);
                    return new Account(generatedAccountId, account.getUsername(), account.getPassword());
                } else {
                    throw new SQLException("Something went wrong during insertion");
                }
            }
        }
        catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }
    
}
