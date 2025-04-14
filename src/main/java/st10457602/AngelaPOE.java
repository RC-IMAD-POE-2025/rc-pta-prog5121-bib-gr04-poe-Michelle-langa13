package st10457602;

import javax.swing.JFrame;

/**
 *
 * @author Angela 
 */
public class AngelaPOE {

    public static void main(String[] args)
    {
        // Create a new Registration object to hold user data
        RegistrationFeature registrationFeature = new RegistrationFeature();
        
        // Create a new Login object, passing in the registration data
        // This is the most important step because without this...
        // The Login instance won't access the Registration instance data
        LoginFeature loginFeature = new LoginFeature(registrationFeature);
        
        // Initialize the GUI panel for user registration and login
        RegistrationScreen registrationScreen = new RegistrationScreen(registrationFeature, loginFeature);
        
        // Close the application when the window is closed
        registrationScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Center the window on the screen
        registrationScreen.setLocationRelativeTo(null);
        
        // Make the window visible
        registrationScreen.setVisible(true);
    }
}
