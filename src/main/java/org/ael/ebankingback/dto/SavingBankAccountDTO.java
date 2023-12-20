package org.ael.ebankingback.dto;


import lombok.Data;

import org.ael.ebankingback.enums.AccountStatus;
import java.util.Date;



@Data
public class SavingBankAccountDTO extends BankAccountDTO{

      private double interestRate;

}
