package st10457602;

import javax.swing.JOptionPane;

/**
 *
 * @author Michelle
 */
public class LoginScreen extends javax.swing.JFrame {

    private LoginFeature loginFeature;
    /**
     * Creates new form LoginPanel
     */
    public LoginScreen(LoginFeature loginFeature) 
    {
        this.loginFeature = loginFeature;
        initComponents();
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginPanel = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        loginTitle1 = new javax.swing.JLabel();
        loginTitle2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 720));
        setSize(new java.awt.Dimension(1280, 720));

        loginPanel.setBackground(new java.awt.Color(237, 192, 207));
        loginPanel.setPreferredSize(new java.awt.Dimension(640, 720));

        usernameLabel.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        usernameLabel.setText("USERNAME:");

        passwordLabel.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        passwordLabel.setText("PASSWORD:");

        usernameField.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N

        passwordField.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N

        loginButton.setBackground(java.awt.Color.green);
        loginButton.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        loginButton.setText("CONTINUE");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoPink.png"))); // NOI18N

        loginTitle1.setFont(new java.awt.Font("Cascadia Code", 1, 24)); // NOI18N
        loginTitle1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginTitle1.setText("Login");

        loginTitle2.setFont(new java.awt.Font("Cascadia Code", 1, 24)); // NOI18N
        loginTitle2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginTitle2.setText("ST10457602, Angela 2025");

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(passwordLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addComponent(loginTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(loginTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(loginTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(usernameLabel)
                .addGap(18, 18, 18)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(passwordLabel)
                .addGap(18, 18, 18)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(loginButton)
                .addContainerGap(203, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        String username, password;

        username = usernameField.getText();
        password =  new String(passwordField.getPassword());
  
        // Attempt to log in the user
        boolean accessGranted = loginFeature.loginUser(username, password);
        
        // Get the login feedback message
        String loginFeedback = loginFeature.returnLoginStatus();

        if (accessGranted)
        {
            // Show success message
            JOptionPane.showMessageDialog(this, loginFeedback, "Access Granted", JOptionPane.INFORMATION_MESSAGE);
            
            // Set the logged-in username in MessageFeature for sender identification
            // The RegistrationFeature object holds the first and last name for display
            MessageFeature.setLoggedInUsername(loginFeature.registrationFeature.getFirstName() + " " + loginFeature.registrationFeature.getLastName());
            
            // Load existing messages from JSON files when entering the message screen
            MessageFeature.loadAllMessagesFromJsonFiles();

            // Create and show the new MessageScreen JFrame, passing necessary features
            MessageScreen messageScreen = new MessageScreen(loginFeature.registrationFeature, loginFeature);
            messageScreen.setVisible(true); // Make the new message screen visible
            messageScreen.setLocationRelativeTo(null); // Center the message screen

            dispose(); // Close the current login screen
        }
        else
        {
            // Show failure message
            JOptionPane.showMessageDialog(this, loginFeedback, "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JLabel loginTitle1;
    private javax.swing.JLabel loginTitle2;
    private javax.swing.JLabel logo;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
