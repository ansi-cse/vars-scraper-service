package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class WalletDTO {
    BigInteger walletId;
    String walletCode;
    String balance;
}
