package org.ael.ebankingback.service;

import org.ael.ebankingback.dto.AccountHistoryDTO;
import org.ael.ebankingback.dto.AccountOperationDTO;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;

import java.util.List;

public interface OperationAccountService {
    List<AccountOperationDTO> accountOperationHistory(String accountId);

    AccountHistoryDTO getAccountOperationHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
