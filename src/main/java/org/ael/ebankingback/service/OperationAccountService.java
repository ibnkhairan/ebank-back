package org.ael.ebankingback.service;

import lombok.NonNull;
import org.ael.ebankingback.dto.AccountHistoryDTO;
import org.ael.ebankingback.dto.AccountOperationDTO;
import org.ael.ebankingback.exceptions.BankAccountNotFoundException;

import java.util.List;

public interface OperationAccountService {
    List<AccountOperationDTO> accountOperationHistory(@NonNull String accountId);

    AccountHistoryDTO getAccountOperationHistory(@NonNull String accountId,@NonNull int page,@NonNull int size) throws BankAccountNotFoundException;
}
