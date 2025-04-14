package st10457602;

/**
 *
 * @author Angela
 */
public class LoginFeature {
    // Reference to the registration details for login validation
    private final RegistrationFeature registrationFeature;

    // Tracks whether the user is currently logged in
    private boolean isLoggedIn;

    // Constructor connects Login to a specific Registration instance
    public LoginFeature(RegistrationFeature registrationFeature) 
    {
        this.registrationFeature = registrationFeature;
        this.isLoggedIn = false; // User starts off as not logged in
    }

    // Attempts to log in using provided username and password
    public boolean loginUser(String username, String password) 
    {
        // Validates the credentials against the registered details
        isLoggedIn = username != null && password != null &&
                     username.equals(registrationFeature.getUsername()) &&
                     password.equals(registrationFeature.getPassword());
        return isLoggedIn;
    }

    // Returns a message based on whether the user is logged in
    public String returnLoginStatus() 
    {
        if (isLoggedIn) {
            return "Welcome back " + registrationFeature.getFirstName() + " " + 
                   registrationFeature.getLastName() + "!\nit is great to see you.";
        } else {
            return "Username or password incorrect, please try again!";
        }
    }
    
}
