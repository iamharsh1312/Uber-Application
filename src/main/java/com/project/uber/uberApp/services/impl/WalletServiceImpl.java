package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.WalletDto;
import com.project.uber.uberApp.dto.WalletTransactionDto;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.Wallet;
import com.project.uber.uberApp.entities.WalletTransaction;
import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.entities.enums.TransactionType;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.WalletRepository;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.services.WalletTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository  walletRepository;
    private final ModelMapper modelMapper;
    private final WalletTransactionService walletTransactionService;



    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount,String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance()+amount);

        WalletTransaction  walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(TransactionMethod.BANKING)
                .amount(amount)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction);


        return walletRepository.save(wallet);


    }

    @Override
    public Wallet deductMoneyFromWallet(User user, Double amount,String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance()-amount);

        WalletTransaction  walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(TransactionMethod.BANKING)
                .amount(amount)
                .build();
        walletTransactionService.createNewWalletTransaction(walletTransaction);
        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {

        return walletRepository.findById(walletId)
                .orElseThrow(()->new ResourceNotFoundException("Wallet not found with Id: " + walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet  =  new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);


    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(()->new ResourceNotFoundException("Wallet not found with Id: " + user));
    }
}
