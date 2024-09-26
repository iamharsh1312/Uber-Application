package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.Wallet;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.WalletRepository;
import com.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository  walletRepository;

    @Override
    public Wallet addMoneyToWallet(User user, Double amount) {

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(()->new ResourceNotFoundException("Wallet not found for user with user id: " + user.getId()));
        wallet.setBalance(wallet.getBalance()+amount);

        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {

        return walletRepository.findById(walletId)
                .orElseThrow(()->new ResourceNotFoundException("Wallet no found with Id: " + walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet  =  new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);


    }
}
