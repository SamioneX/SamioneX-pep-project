package Controller;

import Service.AccountService;
import Service.MessageService;
import Util.UncheckedSQLException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    MessageService messageService = new MessageService();
    AccountService accountService = new AccountService();

    MessageHandlers messageHandlers = new MessageHandlers();
    AccountHandlers accountHandlers = new AccountHandlers();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.exception(UncheckedSQLException.class, this::handleSQLException);

        app.get("example-endpoint", this::exampleHandler);

        app.get("messages", messageHandlers::getAllMessagesHandler);
        app.get("messages/{message_id}", messageHandlers::getMessageByIdHandler);
        app.get("accounts/{account_id}/messages", messageHandlers::findAllMessagesByAUserHandler);

        app.post("register", accountHandlers::registerUserHandler);
        app.post("login", accountHandlers::loginHandler);
        app.post("messages", messageHandlers::createMessageHandler);

        app.delete("messages/{message_id}", messageHandlers::deleteMessageByIdHandler);

        app.patch("messages/{message_id}", messageHandlers::updateMessageByIdHandler);

        return app;
    }

    // Global exception handler for SQLException
    private void handleSQLException(UncheckedSQLException e, Context ctx) {
        ctx.status(500);
        ctx.result("Something went wrong with the database. Please try again later or contact support if this persists.");
        e.printStackTrace();
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private class MessageHandlers {
        private void createMessageHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Message addedMessage = messageService.addMessage(mapper.readValue(ctx.body(), Message.class));
            if (addedMessage != null) {
                ctx.json(mapper.writeValueAsString(addedMessage));
            } else {
                ctx.status(400);
            }
        }

        private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            ctx.json(mapper.writeValueAsString(messageService.getAllMessages()));
        }

        public void getMessageByIdHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Message message = messageService.findMessageById(Integer.parseInt(ctx.pathParam("message_id")));
            if (message != null) {
                ctx.json(mapper.writeValueAsString(message));
            }
        }

        public void deleteMessageByIdHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Message message = messageService.deleteMessageById(Integer.parseInt(ctx.pathParam("message_id")));
            if (message != null) {
                ctx.json(mapper.writeValueAsString(message));
            }
        }

        public void updateMessageByIdHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Message message = mapper.readValue(ctx.body(), Message.class);
            message = messageService.updateMessageById(Integer.parseInt(ctx.pathParam("message_id")), message);
            if (message != null) {
                ctx.json(mapper.writeValueAsString(message));
            } else {
                ctx.status(400);
            }
        }

        public void findAllMessagesByAUserHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            List<Message> messages = messageService.findAllMessagesByAUser(Integer.parseInt(ctx.pathParam("account_id")));
            ctx.json(mapper.writeValueAsString(messages));
        }
    }

    private class AccountHandlers {
        void registerUserHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Account addedAccount = accountService.addAccount(mapper.readValue(ctx.body(), Account.class));
            if (addedAccount != null) {
                ctx.json(mapper.writeValueAsString(addedAccount));
            } else {
                ctx.status(400);
            }
        }
    
        void loginHandler(Context ctx) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            Account userAccount = accountService.authenticateUser(mapper.readValue(ctx.body(), Account.class));
            if (userAccount != null) {
                ctx.json(mapper.writeValueAsString(userAccount));
            } else {
                ctx.status(401);
            }
        }
    }
}