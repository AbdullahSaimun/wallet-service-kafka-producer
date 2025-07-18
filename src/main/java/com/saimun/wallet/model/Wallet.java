package com.saimun.wallet.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Wallet {
	@Id
	private Long userId;
	private double balance;
}
