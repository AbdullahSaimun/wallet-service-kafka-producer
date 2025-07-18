package com.saimun.wallet.controller;

import com.saimun.wallet.dto.TransactionRequest;
import com.saimun.wallet.service.WalletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {
	private final WalletService walletService;

	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}

	@PostMapping("/transaction")
	public String initiateTransaction(@RequestBody TransactionRequest request) {
		return walletService.initiateTransaction(request);
	}


}
