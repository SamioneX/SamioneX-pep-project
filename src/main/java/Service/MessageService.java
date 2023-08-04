package Service;

import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;
import Util.ModelUtils.MessageUtils;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService() {
        messageDAO = MessageDAO.getInstance();
        accountDAO = AccountDAO.getInstance();
    }

    public Message addMessage(Message message) {
        if (MessageUtils.messageIsValid(message, accountDAO::accountWithIdExists)) {
            return messageDAO.insertMessage(message);
        }
        return null;
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message findMessageById(int id) {
        return messageDAO.findMessageById(id);
    }

    public Message deleteMessageById(int id) {
        Message message = findMessageById(id);
        if (message != null) messageDAO.deleteMessageById(id);
        return message;
    }

    public Message updateMessageById(int id, Message msg) {
        if (MessageUtils.messageIsValid(msg)) {
            Message message = findMessageById(id);
            if (message != null) {
                messageDAO.updateMessageTextForId(id, msg.getMessage_text());
                message.setMessage_text(msg.getMessage_text());
                return message;
            }
        }
        return null;
    }

    public List<Message> findAllMessagesByAUser(int account_id) {
        return messageDAO.findAllMessagesByAUser(account_id);
    }
}
