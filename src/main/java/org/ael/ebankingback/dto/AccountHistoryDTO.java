package org.ael.ebankingback.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDTO {
    private String accountId;
    private double balance; //solde
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<AccountOperationDTO> accountOperationDTOS;
}
