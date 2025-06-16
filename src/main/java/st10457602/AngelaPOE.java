package st10457602;

import javax.swing.JFrame;

/**
 * Main class for the QuickChat application.
 * Initializes and manages the flow between registration, login, and messaging screens.
 *
 * @author Angela
 */
public class AngelaPOE {

    public static void main(String[] args)
    {
        // Create a new RegistrationFeature object to manage user registration data
        RegistrationFeature registrationFeature = new RegistrationFeature();
        
        // Create a new LoginFeature object, linking it to the registration data
        // This allows the LoginFeature to validate credentials against registered users
        LoginFeature loginFeature = new LoginFeature(registrationFeature);
        
        // Initialize the GUI panel for user registration (the starting screen)
        RegistrationScreen registrationScreen = new RegistrationScreen(registrationFeature, loginFeature);
        
        // Set the default close operation for the main window
        registrationScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Center the registration window on the screen
        registrationScreen.setLocationRelativeTo(null);
        
        // Make the registration window visible to the user
        registrationScreen.setVisible(true);
    }
}

