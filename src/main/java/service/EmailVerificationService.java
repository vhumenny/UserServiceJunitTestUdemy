package service;

import model.User;

public interface EmailVerificationService {

    void scheduleEmailConfirmation(User user);
}
