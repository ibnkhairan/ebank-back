package org.ael.ebankingback.dto;

import lombok.Data;
import org.ael.ebankingback.enums.AccountStatus;

import java.util.Date;

@Data
public class BankAccountDTO {
    private String id;
    private String type;
    private double balance;//solde
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
}
