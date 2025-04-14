package st10457602;

import java.util.regex.Pattern;

/**
 *
 * @author Angela
 */
public class RegistrationFeature 
{
    private String 
            username, 
            password, 
            cellphone, 
            firstName, 
            lastName;

    // Super method to register a user
    public String registerUser(String username, String password, String cellphone, String firstName, String lastName) {
        String messages = ""; // Build our feedback here
        boolean qualityTest = true; // Track if everything passes

        // Check username first
        if (checkUserName(username)) {
            messages += "Username successfully captured\n";
        } else {
            messages += "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.\n";
            qualityTest = false;
        }

        // Check password
        if (checkPasswordComplexity(password)) {
            messages += "Password successfully captured\n";
        } else {
            messages += "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n";
            qualityTest = false;
        }

        // Check cellphone
        if (checkCellPhoneNumber(cellphone)) {
            messages += "Cellphone number successfully captured\n";
        } else {
            messages += "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.\n";
            qualityTest = false;
        }

        // Check first name
        if (checkName(firstName)) {
            messages += "First name successfully captured\n";
        } else {
            messages += "First name is invalid.\n";
            qualityTest = false;
        }

        // Check last name
        if (checkName(lastName)) {
            messages += "Last name successfully captured\n";
        } else {
            messages += "Last name is invalid.\n";
            qualityTest = false;
        }

        // If all checks pass, save the details
        if (qualityTest) 
        {
            this.username = username;
            this.password = password;
            this.cellphone = cellphone;
            this.firstName = firstName;
            this.lastName = lastName;
            messages += "The Registration process is complete.";
        } else 
        {
            messages += "Please try again.";
        }

        return messages;
    }

    // Getters so LoginLogic can check stuff
    public String getUsername() 
    {
        return username;
    }

    public String getPassword() 
    {
        return password;
    }

    public String getCellphone() 
    {
        return cellphone;
    }

    public String getFirstName() 
    {
        return firstName;
    }

    public String getLastName() 
    {
        return lastName;
    }

    // Validation helpers
    private boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        return password.matches(".*[A-Z].*") && // Capital letter
               password.matches(".*[0-9].*") && // Number
               password.matches(".*[!@#$%^&*()].*"); // Special char
    }

    private boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null && Pattern.matches("^\\+27[0-9]{9}$", cellphone);
    }

    private boolean checkName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z]+$");
    }
    
}
