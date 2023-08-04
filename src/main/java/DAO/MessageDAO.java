package DAO;

import Model.Message;
import java.sql.*;
import Util.ConnectionUtil;
import Util.UncheckedSQLException;

import java.util.*;

public class MessageDAO {
    private static final MessageDAO INSTANCE = new MessageDAO();

    private MessageDAO() {}

    public static MessageDAO getInstance() {
        return INSTANCE;
    }

    public Message insertMessage(Message message) {
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();

            try (ResultSet pkeyResultSet = ps.getGeneratedKeys()) {
                if(pkeyResultSet.next()){
                    int generatedMessageId = (int) pkeyResultSet.getLong(1);
                    return new Message(generatedMessageId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                }
                return null;
            }
        }catch(SQLException e){
            throw new UncheckedSQLException("", e);
        }
    }

    public List<Message> getAllMessages() {
        String sql = "SELECT * FROM message";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            try(ResultSet rs = ps.executeQuery()) {
                List<Message> messages = new ArrayList<>();
                while(rs.next()) {
                    Message message = new Message(
                        rs.getInt(1),
                        rs.getInt(2), 
                        rs.getString(3), 
                        rs.getLong(4)
                    );
                    messages.add(message);
                }
                return messages;
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public Message findMessageById(int id) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return new Message(
                        rs.getInt(1),
                        rs.getInt(2), 
                        rs.getString(3), 
                        rs.getLong(4)
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public void deleteMessageById(int id) {
        String sql = "DELETE FROM message WHERE message_id = ?";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public void updateMessageTextForId(int id, String newMessageText) {
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, newMessageText);
            ps.setInt(2, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }

    public List<Message> findAllMessagesByAUser(int account_id) {
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        try(PreparedStatement ps = ConnectionUtil.getConnection().prepareStatement(sql)) {
            ps.setInt(1, account_id);

            try(ResultSet rs = ps.executeQuery()) {
                List<Message> messages = new ArrayList<>();
                while(rs.next()) {
                    Message message = new Message(
                        rs.getInt(1),
                        rs.getInt(2), 
                        rs.getString(3), 
                        rs.getLong(4)
                    );
                    messages.add(message);
                }
                return messages;
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException("", e);
        }
    }
}
