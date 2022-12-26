package service;

import data.UsersRepository;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UsersRepository usersRepository;
    @Mock
    EmailVerificationService emailVerificationService;
    String firstName;
    String lastName;
    String email;
    String passWord;
    String repeatPassword;

    @BeforeEach
    void init() {
        firstName = "Volodymyr";
        lastName = "Gumennyi";
        email = "test@test.com";
        passWord = "12345678";
        repeatPassword = "12345678";
    }


    @DisplayName("User object created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnUserObject() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenReturn(true);
        //Act
        User user = userService.createUser(firstName, lastName, email, passWord, repeatPassword);

        //Assert
        assertNotNull(user, "the createUserMethod() should not have returned null");
        assertEquals(firstName, user.getFirstName(), "User's first name is incorrect.");
        assertEquals(lastName, user.getLastName(), "User's last name is incorrect.");
        assertEquals(email, user.getEmail(), "User's email is incorrect.");
        assertNotNull(user.getId(), "User id is missing");
        verify(usersRepository).save(any(User.class));
    }

    @DisplayName("Empty first name causes correct exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException() {
        //Arrange
        String firstName = "";
        String expectedExceptionMessage = "User's first name is empty";

        //Act & Assert
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
                        userService.createUser(firstName, lastName, email, passWord, repeatPassword),
                "Empty first name should throw Illegal Argument Exception");

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct.");
    }

    @DisplayName("Empty last name causes correct exception")
    @Test
    void testCreateUser_whenLastNameIsEmpty_throwsIllegalArgumentException() {
        //Arrange
        String lastName = "";
        String expectedExceptionMessage = "User's last name is empty";

        //Act & Assert
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
                        userService.createUser(firstName, lastName, email, passWord, repeatPassword),
                "Empty last name should throw Illegal Argument Exception");

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct.");
    }

    @DisplayName("If save() method causes RuntimeException, a UserServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        //Act & Assert
        assertThrows(UserServiceException.class, () ->
                        userService.createUser(firstName, lastName, email, passWord, repeatPassword),
                "Should have thrown UserServiceException instead");
    }

    @Test
    @DisplayName("EmailNotificationException is handled")
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenReturn(true);

        doThrow(EmailNotificationServiceException.class).
                when(emailVerificationService).scheduleEmailConfirmation(any(User.class));

        //Act & Assert
        assertThrows(UserServiceException.class, () ->
                        userService.createUser(firstName, lastName, email, passWord, repeatPassword),
                "Should have thrown UserServiceException instead");

        //Assert
        verify(emailVerificationService, times(1)).
                scheduleEmailConfirmation(any(User.class));
    }

    @DisplayName("Schedule email confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_schedulesEmailConfirmation() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenReturn(true);

        doCallRealMethod().when(emailVerificationService).
                scheduleEmailConfirmation(any(User.class));
        //Act
        userService.createUser(firstName, lastName, email, passWord, repeatPassword);

        //Assert
        verify(emailVerificationService, times(1)).
                scheduleEmailConfirmation(any(User.class));
    }
}
