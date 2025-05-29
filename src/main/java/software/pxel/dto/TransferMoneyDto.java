package software.pxel.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyDto {
    private BigDecimal amount;
}
