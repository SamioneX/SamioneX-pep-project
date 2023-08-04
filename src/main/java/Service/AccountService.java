package Service;

import DAO.AccountDAO;
import Model.Account;
import Util.ModelUtils.AccountUtils;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = AccountDAO.getInstance();
    }

    public Account addAccount(Account account) {
        if (AccountUtils.accountIsValid(account) 
            && !accountDAO.accountWithUsernameExists(account.getUsername())) {
            return accountDAO.insertAccount(account);
        }
        return null;
    }

    public Account authenticateUser(Account accountInfo) {
        return accountDAO.findAccount(accountInfo);
    }
}
