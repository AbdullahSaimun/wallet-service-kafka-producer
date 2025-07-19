package com.saimun.wallet.service;

import com.saimun.wallet.dto.NotificationKfkaDTO;
import com.saimun.wallet.dto.TransactionRequest;
import com.saimun.wallet.model.Wallet;
import com.saimun.wallet.repo.WalletRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

	private final WalletRepo walletRepo;


	public static final String TOPIC = "transaction-topic";
	private final KafkaTemplate<String , Object> kafkaTemplate;


	@Value("${kafka.topic.transaction}")
	private String transactionTopic;

	@Value("${kafka.topic.notification}")
	private String notificationTopic;


	public WalletService(WalletRepo walletRepo, KafkaTemplate<String, Object> kafkaTemplate) {
		this.walletRepo = walletRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	public String initiateTransaction(TransactionRequest request) {
		Wallet wallet = walletRepo.findById(request.getUserId()).orElse(new Wallet());
		wallet.setUserId(request.getUserId());
		wallet.setBalance(wallet.getBalance() - request.getAmount());
		walletRepo.save(wallet);
		kafkaTemplate.send(transactionTopic, request.getUserId().toString(), request);
		return "Transaction initiated for user: " + request.getUserId().toString();

	}

	public String initiateNotificaion() {

		for(int i= 0; i< 100; i++) {
			NotificationKfkaDTO dto = new NotificationKfkaDTO();
			dto.setName("first_"+i);
			dto.setMessage("msg_"+i);
			kafkaTemplate.send(notificationTopic, dto);

		}

		return "Notification initiated";


	}


}
