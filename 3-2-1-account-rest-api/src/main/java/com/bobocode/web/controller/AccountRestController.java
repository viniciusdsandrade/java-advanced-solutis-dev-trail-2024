package com.bobocode.web.controller;

import com.bobocode.dao.AccountDao;
import com.bobocode.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * <p>
 * todo: 1. Configure rest controller that handles requests with url "/accounts"
 * todo: 2. Inject {@link AccountDao} implementation
 * todo: 3. Implement method that handles GET request and returns a list of accounts
 * todo: 4. Implement method that handles GET request with id as path variable and returns account by id
 * todo: 5. Implement method that handles POST request, receives account as request body, saves account and returns it
 * todo:    Configure HTTP response status code 201 - CREATED
 * todo: 6. Implement method that handles PUT request with id as path variable and receives account as request body.
 * todo:    It check if account id and path variable are the same and throws {@link IllegalStateException} otherwise.
 * todo:    Then it saves received account. Configure HTTP response status code 204 - NO CONTENT
 * todo: 7. Implement method that handles DELETE request with id as path variable removes an account by id
 * todo:    Configure HTTP response status code 204 - NO CONTENT
 */
@RestController // Define o controlador como um REST Controller
@RequestMapping("/accounts") // Define o caminho base para as requisições
public class AccountRestController {

    private final AccountDao accountDao;

    @Autowired
    public AccountRestController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping // Handle GET request for all accounts
    public List<Account> getAllAccounts() {
        return accountDao.findAll();
    }

    @GetMapping("/{id}") // Handle GET request for a specific account by ID
    public Account getAccountById(@PathVariable long id) {
        return accountDao.findById(id);
    }

    @PostMapping // Handle POST request to save a new account
    @ResponseStatus(CREATED) // Return 201 status code
    public Account createAccount(@RequestBody Account account) {
        return accountDao.save(account);
    }

    @PutMapping("/{id}") // Handle PUT request to update an account
    @ResponseStatus(NO_CONTENT) // Return 204 status code
    public void updateAccount(@PathVariable Long id, @RequestBody Account account) {
        if (!id.equals(account.getId())) {
            throw new IllegalStateException("ID mismatch");
        }
        accountDao.save(account);
    }

    @DeleteMapping("/{id}") // Handle DELETE request to remove an account by ID
    @ResponseStatus(NO_CONTENT) // Return 204 status code
    public void deleteAccount(@PathVariable long id) {
        accountDao.remove(accountDao.findById(id));
    }
}
