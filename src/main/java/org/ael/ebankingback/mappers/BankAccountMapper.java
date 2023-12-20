package org.ael.ebankingback.mappers;

import org.ael.ebankingback.dto.AccountOperationDTO;
import org.ael.ebankingback.dto.CurrentBankAccountDTO;
import org.ael.ebankingback.dto.SavingBankAccountDTO;
import org.ael.ebankingback.entities.AccountOperation;
import org.ael.ebankingback.entities.CurrentAccount;
import org.ael.ebankingback.entities.SavingAccount;

public interface BankAccountMapper {
    SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount);
    SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO);
    CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount);
    CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO);
    AccountOperationDTO fromAccountOperation(AccountOperation accountOperation);
}
