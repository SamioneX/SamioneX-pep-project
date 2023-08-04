package Util;

import java.sql.SQLException;

public class UncheckedSQLException extends RuntimeException {
    public UncheckedSQLException(String message, SQLException cause) {
        super(message, cause);
    }
}
