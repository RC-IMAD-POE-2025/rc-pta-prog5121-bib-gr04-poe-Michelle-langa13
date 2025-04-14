package st10457602;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of the LoginFeature class.
 */
public class LoginFeatureTest {

    /** The RegistrationFeature instance for storing test user data. */
    private RegistrationFeature registrationQATest;

    /** The LoginFeature instance under test, linked to the RegistrationFeature. */
    private LoginFeature loginQATest;

    // Test data
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String VALID_CELL_PHONE_NUMBER = "+27838968976";
    private static final String VALID_FIRST_NAME = "Kyle";
    private static final String VALID_LAST_NAME = "Smith";
    private static final String INVALID_USERNAME = "kyle!!!!!!!";
    private static final String INVALID_PASSWORD = "password";

    // Expected messages
    private static final String SUCCESS_LOGIN_MESSAGE = "Welcome back " + VALID_FIRST_NAME + " " + VALID_LAST_NAME + "!\nit is great to see you.";
    private static final String FAILED_LOGIN_MESSAGE = "Username or password incorrect, please try again!";

    @BeforeEach
    public void setUp() {
        registrationQATest = new RegistrationFeature();
        registrationQATest.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        loginQATest = new LoginFeature(registrationQATest);
    }

    /**
     * Tests successful login with valid credentials.
     */
    @Test
    void testSuccessfulLogin() {
        boolean result = loginQATest.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(result, "Login should succeed with valid credentials");
        assertEquals(SUCCESS_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Success message should match");
    }

    /**
     * Tests login failure with invalid credentials.
     */
    @Test
    void testInvalidCredentials() {
        // Invalid username
        boolean result1 = loginQATest.loginUser(INVALID_USERNAME, VALID_PASSWORD);
        assertFalse(result1, "Login should fail with invalid username");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");

        // Invalid password
        boolean result2 = loginQATest.loginUser(VALID_USERNAME, INVALID_PASSWORD);
        assertFalse(result2, "Login should fail with invalid password");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");
    }

    /**
     * Tests login failure with null or empty credentials.
     */
    @Test
    void testNullOrEmptyCredentials() {
        // Null username
        boolean result1 = loginQATest.loginUser(null, VALID_PASSWORD);
        assertFalse(result1, "Login should fail with null username");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");

        // Empty password
        boolean result2 = loginQATest.loginUser(VALID_USERNAME, "");
        assertFalse(result2, "Login should fail with empty password");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");
    }

    /**
     * Tests login failure with case-sensitive credentials.
     */
    @Test
    void testCaseSensitiveCredentials() {
        // Case-sensitive username
        boolean result1 = loginQATest.loginUser(VALID_USERNAME.toUpperCase(), VALID_PASSWORD);
        assertFalse(result1, "Login should fail with wrong username case");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");

        // Case-sensitive password
        boolean result2 = loginQATest.loginUser(VALID_USERNAME, VALID_PASSWORD.toLowerCase());
        assertFalse(result2, "Login should fail with wrong password case");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");
    }

    /**
     * Tests multiple login attempts (invalid followed by valid).
     */
    @Test
    void testMultipleLoginAttempts() {
        // Invalid attempt
        boolean result1 = loginQATest.loginUser(INVALID_USERNAME, INVALID_PASSWORD);
        assertFalse(result1, "Login should fail with invalid credentials");
        assertEquals(FAILED_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Failure message should match");

        // Valid attempt
        boolean result2 = loginQATest.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(result2, "Login should succeed with valid credentials");
        assertEquals(SUCCESS_LOGIN_MESSAGE, loginQATest.returnLoginStatus(), "Success message should match");
    }
}