package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.WalletTransactionDto;
import com.project.uber.uberApp.entities.WalletTransaction;
import com.project.uber.uberApp.repositories.WalletTransactionRepository;
import com.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper modelMapper;
    @Override
    public void createNewWalletTransaction(WalletTransactionDto walletTransactionDto) {
        WalletTransaction walletTransaction = modelMapper.map(walletTransactionDto, WalletTransaction.class);
        WalletTransactionRepository.save(walletTransaction);
    }
}
