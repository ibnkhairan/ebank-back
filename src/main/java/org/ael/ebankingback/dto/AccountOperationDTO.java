package org.ael.ebankingback.dto;



import lombok.Data;
import org.ael.ebankingback.enums.OperationType;
import java.util.Date;

@Data
public class AccountOperationDTO {

    private Long id;
    private String description;
    private Date operationDate;
    private double amount;
    private OperationType type;

}
