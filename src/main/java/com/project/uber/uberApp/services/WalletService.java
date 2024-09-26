package com.project.uber.uberApp.services;

import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.Wallet;

public interface WalletService {

    Wallet addMoneyToWallet(Long userId, Double amount);
    void withdrawAllMyMoneyFromWallet();

    Wallet findWalletById(Long userId);

    Wallet createNewWallet(User user);

}
