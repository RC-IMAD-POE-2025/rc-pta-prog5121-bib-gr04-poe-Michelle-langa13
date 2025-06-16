package st10457602;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Handles the user interface for messaging features using a JFrame.
 * This class allows users to send, store, disregard messages, and view reports
 * based on the QuickChat application requirements.
 *
 * @author Angela
 */
public class MessageScreen extends JFrame {

    private RegistrationFeature registrationFeature; // To access user details
    private LoginFeature loginFeature; // To manage session or return to login

    // GUI Components
    private JPanel mainPanel;
    private JTextArea displayArea;
    private JScrollPane scrollPane;
    private JButton sendMessageButton;
    private JButton showAllSentButton;
    private JButton showLongestMessageButton;
    private JButton searchMessageIdButton;
    private JButton searchRecipientButton;
    private JButton deleteMessageButton;
    private JButton generateReportButton;
    private JButton logoutButton;

    /**
     * Creates a new MessageScreen JFrame.
     *
     * @param registrationFeature The RegistrationFeature object to get user details.
     * @param loginFeature The LoginFeature object to handle logout.
     */
    public MessageScreen(RegistrationFeature registrationFeature, LoginFeature loginFeature) {
        this.registrationFeature = registrationFeature;
        this.loginFeature = loginFeature;
        initComponents();
        setupLayout();
        addListeners();
        // Display initial welcome message
        displayMessage("Welcome to QuickChat v2, " + registrationFeature.getFirstName() + "!");
        // Load any existing messages from JSON files on startup
        MessageFeature.loadAllMessagesFromJsonFiles();
    }

    /**
     * Initializes the GUI components.
     */
    private void initComponents() {
        setTitle("QuickChat Messaging - ST10457602, Angela 2025");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(237, 192, 207)); // Pink background
        mainPanel.setLayout(new BorderLayout(10, 10)); // Add gaps between components

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBackground(new Color(245, 245, 245)); // Light grey background for text area
        displayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)); // Border for scroll pane

        // Initialize buttons
        sendMessageButton = new JButton("1. Send/Store Messages");
        showAllSentButton = new JButton("2. Show All Sent Messages Info");
        showLongestMessageButton = new JButton("3. Show Longest Sent Message");
        searchMessageIdButton = new JButton("4. Search Message by ID");
        searchRecipientButton = new JButton("5. Search Messages by Recipient");
        deleteMessageButton = new JButton("6. Delete Message by Hash");
        generateReportButton = new JButton("7. Generate Full Sent Report");
        logoutButton = new JButton("8. Logout");

        // Set button styles
        styleButton(sendMessageButton, Color.GREEN.darker());
        styleButton(showAllSentButton, Color.BLUE.darker());
        styleButton(showLongestMessageButton, Color.BLUE.darker());
        styleButton(searchMessageIdButton, Color.BLUE.darker());
        styleButton(searchRecipientButton, Color.BLUE.darker());
        styleButton(deleteMessageButton, Color.ORANGE.darker());
        styleButton(generateReportButton, Color.BLUE.darker());
        styleButton(logoutButton, Color.RED.darker());
    }

    /**
     * Sets up the layout of the GUI components.
     */
    private void setupLayout() {
        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(237, 192, 207));
        buttonPanel.setLayout(new GridLayout(8, 1, 10, 10)); // 8 rows, 1 column, 10px gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        buttonPanel.add(sendMessageButton);
        buttonPanel.add(showAllSentButton);
        buttonPanel.add(showLongestMessageButton);
        buttonPanel.add(searchMessageIdButton);
        buttonPanel.add(searchRecipientButton);
        buttonPanel.add(deleteMessageButton);
        buttonPanel.add(generateReportButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST); // Buttons on the right

        add(mainPanel); // Add the main panel to the JFrame
    }

    /**
     * Adds action listeners to the buttons.
     */
    private void addListeners() {
        sendMessageButton.addActionListener(e -> processMessageBatch());
        showAllSentButton.addActionListener(e -> displayMessage(MessageFeature.displayAllSentMessagesInfo()));
        showLongestMessageButton.addActionListener(e -> displayMessage("Longest Sent Message:\n\"" + MessageFeature.findLongestSentMessage() + "\""));
        searchMessageIdButton.addActionListener(e -> handleSearchMessageByID());
        searchRecipientButton.addActionListener(e -> handleSearchMessagesByRecipient());
        deleteMessageButton.addActionListener(e -> handleDeleteMessageByHash());
        generateReportButton.addActionListener(e -> displayMessage(MessageFeature.generateSentMessagesReport()));
        logoutButton.addActionListener(e -> handleLogout());
    }

    /**
     * Displays a message in the main text area.
     * @param message The message to display.
     */
    private void displayMessage(String message) {
        displayArea.setText(message);
        // Scroll to the top to see the new message
        displayArea.setCaretPosition(0);
    }

    /**
     * Styles a given JButton.
     * @param button The JButton to style.
     * @param bgColor The background color for the button.
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Cascadia Code", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1), // Darker border
                BorderFactory.createEmptyBorder(10, 15, 10, 15))); // Padding
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
    }

    /**
     * Handles the process of entering multiple messages.
     * This is adapted from the original JOptionPane flow.
     */
    private void processMessageBatch() {
        String numMessagesStr = JOptionPane.showInputDialog(this,
                "How many messages would you like to process?",
                "Number of Messages", JOptionPane.QUESTION_MESSAGE);

        if (numMessagesStr == null || numMessagesStr.trim().isEmpty()) {
            displayMessage("Number of messages not provided. Returning to main menu.");
            return;
        }

        int numMessages;
        try {
            numMessages = Integer.parseInt(numMessagesStr);
        } catch (NumberFormatException e) {
            displayMessage("Invalid number entered. Please use digits.");
            return;
        }

        if (numMessages <= 0) {
            displayMessage("Please enter a positive number of messages.");
            return;
        }

        StringBuilder batchSummary = new StringBuilder("--- Message Batch Processing ---\n");
        int sentCount = 0;

        for (int i = 0; i < numMessages; i++) {
            batchSummary.append("Processing Message ").append(i + 1).append(" of ").append(numMessages).append("...\n");

            String recipient = JOptionPane.showInputDialog(this, "Enter recipient's cell number (e.g., +27718693002):", "Message " + (i + 1) + " - Recipient", JOptionPane.PLAIN_MESSAGE);
            if (recipient == null) {
                batchSummary.append("Recipient input cancelled for Message ").append(i + 1).append(". Skipping.\n");
                continue;
            }

            String payload = JOptionPane.showInputDialog(this, "Enter message payload:", "Message " + (i + 1) + " - Payload", JOptionPane.PLAIN_MESSAGE);
            if (payload == null) {
                batchSummary.append("Payload input cancelled for Message ").append(i + 1).append(". Skipping.\n");
                continue;
            }

            MessageFeature currentMessage = new MessageFeature(recipient, payload);
            batchSummary.append(currentMessage.getGeneratedIdNotification()).append("\n");

            // Perform validations
            String recipientValidationMsg = currentMessage.validateRecipientNumber(currentMessage.getMessageRecipient());
            if (!recipientValidationMsg.equals("Cell phone number successfully captured.")) {
                batchSummary.append("Validation Failed for Message ").append(i + 1).append(":\n").append(recipientValidationMsg).append("\n");
                continue; // Skip to next message
            }

            String payloadValidationMsg = currentMessage.validatePayloadLength(currentMessage.getMessagePayload());
            if (!payloadValidationMsg.equals("Message ready to send.")) {
                batchSummary.append("Validation Failed for Message ").append(i + 1).append(":\n").append(payloadValidationMsg).append("\n");
                continue; // Skip to next message
            }

            String[] options = {"Send Message", "Store Message", "Disregard Message"};
            int actionChoice = JOptionPane.showOptionDialog(this,
                    "Choose an action for this message:\nTo: " + currentMessage.getMessageRecipient() + "\nMessage: " + currentMessage.getMessagePayload().substring(0, Math.min(currentMessage.getMessagePayload().length(), 50)) + (currentMessage.getMessagePayload().length() > 50 ? "..." : ""),
                    "Message " + (i + 1) + " - Action",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            String resultMessage;
            switch (actionChoice) {
                case 0: // Send Message
                    resultMessage = currentMessage.sentMessage();
                    batchSummary.append("Send Status: ").append(resultMessage).append("\n");
                    if (resultMessage.equals("Message successfully sent.")) {
                        sentCount++;
                        // Storing the sent message to file is part of the original logic when sent
                        String storeSentResult = currentMessage.storeMessage(); // Also store sent message
                        batchSummary.append("  (Stored to file as part of sending: ").append(storeSentResult).append(")\n");
                    }
                    break;
                case 1: // Store Message
                    resultMessage = currentMessage.storeMessage();
                    batchSummary.append("Store Status: ").append(resultMessage).append("\n");
                    break;
                case 2: // Disregard Message
                    currentMessage.disregardMessage(); // Mark as disregarded and add to list
                    batchSummary.append("Message disregarded by user.\n");
                    break;
                default:
                    batchSummary.append("No action selected for message ").append(i + 1).append(".\n");
                    break;
            }
            batchSummary.append("\n"); // Add a blank line for readability between messages
        }
        batchSummary.append("--- Batch Processing Complete ---\n");
        batchSummary.append("Total messages successfully sent in this session: ").append(sentCount).append("\n");
        batchSummary.append("Overall total messages sent: ").append(MessageFeature.returnTotalMessages());
        displayMessage(batchSummary.toString());
    }

    /**
     * Handles the search for a message by its ID.
     */
    private void handleSearchMessageByID() {
        String searchID = JOptionPane.showInputDialog(this,
                "Enter the Message ID to search for:",
                "Search Message by ID", JOptionPane.QUESTION_MESSAGE);

        if (searchID == null || searchID.trim().isEmpty()) {
            displayMessage("Search cancelled or no ID entered.");
            return;
        }

        String result = MessageFeature.searchMessageByID(searchID);
        displayMessage(result);
    }

    /**
     * Handles the search for messages by a recipient's number.
     */
    private void handleSearchMessagesByRecipient() {
        String searchRecipient = JOptionPane.showInputDialog(this,
                "Enter the Recipient's Cell Number to search for:",
                "Search Messages by Recipient", JOptionPane.QUESTION_MESSAGE);

        if (searchRecipient == null || searchRecipient.trim().isEmpty()) {
            displayMessage("Search cancelled or no recipient entered.");
            return;
        }

        String result = MessageFeature.searchMessagesByRecipient(searchRecipient);
        displayMessage(result);
    }

    /**
     * Handles the deletion of a message by its hash.
     */
    private void handleDeleteMessageByHash() {
        String hashToDelete = JOptionPane.showInputDialog(this,
                "Enter the Message Hash to delete:",
                "Delete Message by Hash", JOptionPane.QUESTION_MESSAGE);

        if (hashToDelete == null || hashToDelete.trim().isEmpty()) {
            displayMessage("Deletion cancelled or no hash entered.");
            return;
        }

        String result = MessageFeature.deleteMessageByHash(hashToDelete);
        displayMessage(result);
    }

    /**
     * Handles the logout process, closing the message screen and opening the registration screen.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Clear any session-specific data if necessary
            MessageFeature.resetMessageCounterForTesting(); // Resets static data for a clean start next time
            
            // Create a new registration screen and make it visible
            RegistrationScreen registrationScreen = new RegistrationScreen(new RegistrationFeature(), new LoginFeature(new RegistrationFeature()));
            registrationScreen.setVisible(true);
            registrationScreen.setLocationRelativeTo(null);
            
            dispose(); // Close the current message screen
        }
    }
}
