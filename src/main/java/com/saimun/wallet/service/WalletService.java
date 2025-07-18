package com.saimun.wallet.service;

import com.saimun.wallet.dto.TransactionRequest;
import com.saimun.wallet.model.Wallet;
import com.saimun.wallet.repo.WalletRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

	private final WalletRepo walletRepo;


	public static final String TOPIC = "transaction-topic";
	private final KafkaTemplate<String , TransactionRequest> kafkaTemplate;


	public WalletService(WalletRepo walletRepo, KafkaTemplate<String, TransactionRequest> kafkaTemplate) {
		this.walletRepo = walletRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	public String initiateTransaction(TransactionRequest request) {
		Wallet wallet = walletRepo.findById(request.getUserId()).orElse(new Wallet());
		wallet.setUserId(request.getUserId());
		wallet.setBalance(wallet.getBalance() - request.getAmount());
		walletRepo.save(wallet);
		kafkaTemplate.send(TOPIC, request.getUserId().toString(), request);
		return "Transaction initiated for user: " + request.getUserId().toString();

	}


}
