package com.home.user.service.service;


/**
 * EmailService provides a collection of methods to handle sending confirmation emails
 * for various user-related operations. This service focuses on sending emails based
 * on specific actions performed on a user account, such as account creation, update,
 * deletion, or adding a product for sale.
 */
public interface EmailService {

    void sendProductAddForSellConfirmation(String email);

    void sendAccountDeletionConfirmation(String email);

    void sendAccountCompletionConfirmation(String email);

    void sendAccountUpdateConfirmation(String email);
}
