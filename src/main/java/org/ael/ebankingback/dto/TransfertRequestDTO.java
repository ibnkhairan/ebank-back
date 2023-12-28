package org.ael.ebankingback.dto;

import lombok.Data;

@Data
public class TransfertRequestDTO {
    private String accountIdSource;
    private String accountDestination;
    private double amount;
    private String description;
}
