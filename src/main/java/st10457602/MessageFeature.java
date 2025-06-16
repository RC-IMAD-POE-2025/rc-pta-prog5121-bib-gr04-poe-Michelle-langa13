package st10457602;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Represents a single message in the QuickChat application.
 * Handles message creation, validation, hashing, sending, and storage.
 * This class also manages collections of all sent, stored, and disregarded messages
 * for reporting and management purposes as per Part 3 requirements.
 *
 * @author Angela
 */
public class MessageFeature {
    private final String MESSAGE_ID;
    private final String MESSAGE_RECIPIENT;
    private final String MESSAGE_PAYLOAD;
    private int MESSAGE_INDEX;
    private String MESSAGE_HASH;
    // Tracks if the message was sent (true), stored (false), or disregarded (false)
    private String messageStatus; // Added to track status for reporting (Sent, Stored, Disregarded)

    // Static counter for unique indexing of sent messages
    private static int messageDispatchCounter = 0;

    private static final int MAX_PAYLOAD_LENGTH = 250;
    private static final Random idGeneratorRandom = new Random();

    // --- Static ArrayLists for Part 3: Storing all messages ---
    // These lists hold MessageFeature objects to keep all details together
    private static ArrayList<MessageFeature> allSentMessages = new ArrayList<>();
    private static ArrayList<MessageFeature> allDisregardedMessages = new ArrayList<>();
    private static ArrayList<MessageFeature> allStoredMessages = new ArrayList<>(); // For messages explicitly stored

    // As per POE, also keeping separate lists for IDs and Hashes (can be redundant but required)
    private static ArrayList<String> allMessageHashes = new ArrayList<>();
    private static ArrayList<String> allMessageIDs = new ArrayList<>();

    // Stores the username of the currently logged-in user for sender identification
    private static String loggedInUsername = "";

    /**
     * Sets the username of the user who is currently logged in.
     * This is used to identify the sender of messages.
     * @param username The username of the logged-in user.
     */
    public static void setLoggedInUsername(String username) {
        MessageFeature.loggedInUsername = username;
    }

    /**
     * Constructs a new Message.
     *
     * @param recipient The recipient's cellphone number.
     * @param payload   The content of the message.
     */
    public MessageFeature(final String recipient, final String payload) {
        // Generate a 10-digit random number string for MESSAGE_ID
        // Using Math.abs and "% 10000000000L" ensures a non-negative 10-digit number
        this.MESSAGE_ID = String.format("%010d", Math.abs(idGeneratorRandom.nextLong() % 10000000000L));
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = 0; // Initial index for a new (unsent) message
        this.MESSAGE_HASH = "";   // Initial hash is empty
        this.messageStatus = "New"; // Default status
    }

    // --- Getters ---
    public String getMessageID() { return MESSAGE_ID; }
    public String getMessageRecipient() { return MESSAGE_RECIPIENT; }
    public String getMessagePayload() { return MESSAGE_PAYLOAD; }
    public int getMessageIndex() { return MESSAGE_INDEX; }
    public String getMessageHash() { return MESSAGE_HASH; }
    public String getMessageStatus() { return messageStatus; }

    /**
     * Validates the format of a given message ID (10 digits).
     *
     * @param id The message ID string.
     * @return true if valid, false otherwise.
     */
    public boolean checkMessageID(final String id) {
        if (id == null) {
            return false;
        }
        return id.matches("\\d{10}");
    }

    /**
     * Validates the message payload length.
     *
     * @param payload The message content.
     * @return A status string: "Message ready to send." or an error message with details.
     */
    public String validatePayloadLength(final String payload) {
        if (payload == null) {
            // Null payload is treated as exceeding length by the max length itself
            return "Message exceeds " + MAX_PAYLOAD_LENGTH + " characters by " + (0 - MAX_PAYLOAD_LENGTH) + ", please reduce size.";
        }
        if (payload.length() <= MAX_PAYLOAD_LENGTH) {
            return "Message ready to send.";
        } else {
            int excess = payload.length() - MAX_PAYLOAD_LENGTH;
            return "Message exceeds " + MAX_PAYLOAD_LENGTH + " characters by " + excess + ", please reduce size.";
        }
    }

    /**
     * Validates the recipient's cellphone number format (+27 followed by 9 digits).
     *
     * @param recipient The cellphone number.
     * @return A status string: "Cell phone number successfully captured." or an error message.
     */
    public String validateRecipientNumber(final String recipient) {
        if (recipient == null || recipient.isBlank()) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        String regexSA = "^\\+27[0-9]{9}$"; // Regex for +27 followed by 9 digits
        if (Pattern.matches(regexSA, recipient)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    /**
     * Creates a message hash based on message ID, index, and payload.
     * Format: FirstTwoCharsOfID:Index:FirstWordOfPayloadLastWordOfPayload (ALL CAPS).
     * Punctuation is removed from the first and last words.
     *
     * @param id      The message ID.
     * @param index   The message index.
     * @param payload The message content.
     * @return The uppercase hash string, or empty if inputs are invalid.
     */
    public String createMessageHash(final String id, int index, final String payload) {
        if (id == null || id.length() < 2 || payload == null) {
            return ""; // Cannot generate hash with invalid inputs
        }

        String idStart = id.substring(0, 2);
        String content = payload.trim(); // Trim whitespace from payload

        if (content.isEmpty()) {
            return (idStart + ":" + index + ":").toUpperCase(); // Handle empty payload gracefully
        }

        String[] words = content.split("\\s+"); // Split by one or more whitespace characters
        String firstWord = words[0];
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        // Remove non-alphanumeric characters from words as per POE example "HITONIGHT"
        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        return (idStart + ":" + index + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Processes the message for sending: validates, assigns index, generates hash,
     * and adds to the list of sent messages.
     *
     * @return A status string: "Message successfully sent." or an error message.
     */
    public String sentMessage() {
        // Check for empty or whitespace-only payload
        if (this.MESSAGE_PAYLOAD == null || this.MESSAGE_PAYLOAD.trim().isEmpty()) {
            return "Failed to send message: Message content cannot be empty";
        }
        
        // Validate payload length
        String payloadValMsg = validatePayloadLength(this.MESSAGE_PAYLOAD);
        if (!payloadValMsg.equals("Message ready to send.")) {
            if (this.MESSAGE_PAYLOAD.length() > MAX_PAYLOAD_LENGTH) {
                 return "Failed to send message: Payload too long";
            }
            return payloadValMsg; // Return specific error from validation
        }

        // Validate recipient number
        String recipientValMsg = validateRecipientNumber(this.MESSAGE_RECIPIENT);
        if (!recipientValMsg.equals("Cell phone number successfully captured.")) {
            return "Failed to send message: Invalid recipient";
        }

        // Check message ID validity (should always be valid due to generation logic)
        if (!checkMessageID(this.MESSAGE_ID)) {
            return "Failed to send message: Invalid message ID (system error)";
        }

        messageDispatchCounter++;
        this.MESSAGE_INDEX = messageDispatchCounter;
        // Generate message hash
        this.MESSAGE_HASH = createMessageHash(this.MESSAGE_ID, this.MESSAGE_INDEX, this.MESSAGE_PAYLOAD);
        this.messageStatus = "Sent"; // Update message status to Sent

        // Add to global lists for reporting
        allSentMessages.add(this); // Add this message object to the list of sent messages
        allMessageIDs.add(this.MESSAGE_ID); // Add its ID
        allMessageHashes.add(this.MESSAGE_HASH); // Add its hash

        return "Message successfully sent.";
    }
    
    /**
     * Marks the current message as disregarded and adds it to the disregarded messages list.
     */
    public void disregardMessage() {
        this.messageStatus = "Disregarded"; // Set status for disregarded message
        allDisregardedMessages.add(this); // Add to the list of disregarded messages
        // IDs and Hashes for disregarded messages are not explicitly required by POE,
        // so not adding to global allMessageIDs/allMessageHashes here.
    }

    /**
     * Stores the current message to a JSON file (message_INDEX.json or message_draft_ID.json).
     * If the message's status is "New", it is set to "Stored". Otherwise, its existing status is preserved.
     * A hash is generated if it's currently empty, especially for drafts.
     *
     * @return Status string: "Message successfully stored." or an error message.
     */
    public String storeMessage() {
        JSONObject msgJson = new JSONObject();
        msgJson.put("MESSAGE_ID", this.MESSAGE_ID);
        msgJson.put("MESSAGE_RECIPIENT", this.MESSAGE_RECIPIENT);
        msgJson.put("MESSAGE_PAYLOAD", this.MESSAGE_PAYLOAD);
        msgJson.put("MESSAGE_INDEX", this.MESSAGE_INDEX); // Will be 0 for drafts, >0 for sent

        // Generate hash for stored messages if not already generated (e.g., for drafts)
        if (this.MESSAGE_HASH.isEmpty()) {
            this.MESSAGE_HASH = createMessageHash(this.MESSAGE_ID, this.MESSAGE_INDEX, this.MESSAGE_PAYLOAD);
        }
        msgJson.put("MESSAGE_HASH", this.MESSAGE_HASH);

        // Determine the status to save in JSON based on current internal status
        String statusToSaveInJson = this.messageStatus;
        if (this.messageStatus.equals("New")) {
            // If it's a new message being explicitly stored, its status becomes "Stored"
            this.messageStatus = "Stored"; // Update internal object state
            statusToSaveInJson = "Stored"; // This is the status that goes into JSON
        }
        // If it was already "Sent" or "Disregarded", its status remains unchanged,
        // and that's what's saved in JSON.
        msgJson.put("MESSAGE_STATUS", statusToSaveInJson);

        String fileName;
        // For drafts (MESSAGE_INDEX == 0), use a unique name to avoid overwriting "message_0.json"
        if (this.MESSAGE_INDEX == 0) {
            fileName = "message_draft_" + this.MESSAGE_ID + ".json";
        } else {
            // For sent messages, use their unique index for file naming
            fileName = "message_" + this.MESSAGE_INDEX + ".json";
        }
        
        // Add to allStoredMessages if not already in the list (prevents duplicates)
        if (!allStoredMessages.contains(this)) {
            allStoredMessages.add(this);
        }
        // Ensure IDs and Hashes are in global lists, especially for stored-only messages
        if (!allMessageIDs.contains(this.MESSAGE_ID)) {
            allMessageIDs.add(this.MESSAGE_ID);
        }
        if (!this.MESSAGE_HASH.isEmpty() && !allMessageHashes.contains(this.MESSAGE_HASH)) {
            allMessageHashes.add(this.MESSAGE_HASH);
        }

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(msgJson.toJSONString());
            return "Message successfully stored.";
        } catch (IOException e) {
            // No console output as per user's request
            return "Failed to store message: IO Exception.";
        }
    }

    /**
     * Returns a notification string confirming Message ID generation.
     *
     * @return Formatted string with the message ID.
     */
    public String getGeneratedIdNotification() {
        return "Message ID generated: " + this.MESSAGE_ID;
    }

    /**
     * Returns the total number of successfully sent messages.
     * (This count only refers to messages processed via sentMessage(), not stored drafts.)
     * @return Total sent messages count.
     */
    public static int returnTotalMessages() {
        return messageDispatchCounter;
    }

    /**
     * Resets static counters and lists for testing purposes.
     * Ensures test independence by clearing in-memory data and deleting generated JSON files.
     */
    public static void resetMessageCounterForTesting() {
        messageDispatchCounter = 0;
        allSentMessages.clear();
        allDisregardedMessages.clear();
        allStoredMessages.clear();
        allMessageHashes.clear();
        allMessageIDs.clear();
        loggedInUsername = ""; // Clear logged in user for tests
        // Also clear any message JSON files generated during tests
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.matches("message_.*\\.json|message_draft_.*\\.json"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
    
    // --- New Methods for Part 3 Reports and Data Management ---

    /**
     * Loads all message data from JSON files in the current directory into the static lists.
     * This method is crucial for persisting and retrieving messages between application runs.
     * It parses each JSON file and reconstructs MessageFeature objects, populating
     * the appropriate static lists (allStoredMessages, allSentMessages, etc.) based on their status.
     * This method attempts to load *all* message JSON files and categorize them.
     */
    public static void loadAllMessagesFromJsonFiles() {
        // Clear existing static lists to prevent duplicates on successive loads (e.g., during testing or re-initialization)
        allSentMessages.clear();
        allDisregardedMessages.clear();
        allStoredMessages.clear();
        allMessageHashes.clear();
        allMessageIDs.clear();
        messageDispatchCounter = 0; // Reset counter, will be updated by loaded sent messages

        File currentDir = new File(".");
        // Filter for files starting with "message_" or "message_draft_" and ending with ".json"
        File[] files = currentDir.listFiles((dir, name) -> name.matches("message_.*\\.json|message_draft_.*\\.json"));

        if (files == null) {
            return; // No console output
        }

        JSONParser parser = new JSONParser();

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;

                // Extract data from JSON object
                String id = (String) jsonObject.get("MESSAGE_ID");
                String recipient = (String) jsonObject.get("MESSAGE_RECIPIENT");
                String payload = (String) jsonObject.get("MESSAGE_PAYLOAD");
                Long indexLong = (Long) jsonObject.get("MESSAGE_INDEX");
                int index = (indexLong != null) ? indexLong.intValue() : 0;
                String hash = (String) jsonObject.get("MESSAGE_HASH");
                String status = (String) jsonObject.get("MESSAGE_STATUS");

                // Reconstruct MessageFeature object
                MessageFeature loadedMessage = new MessageFeature(recipient, payload);
                loadedMessage.MESSAGE_INDEX = index;
                loadedMessage.MESSAGE_HASH = (hash != null) ? hash : ""; // Ensure hash is not null
                loadedMessage.messageStatus = (status != null) ? status : (index == 0 ? "Stored" : "Sent"); // Default for old files

                // Add to appropriate lists based on status
                if (loadedMessage.getMessageStatus().equals("Sent")) {
                    allSentMessages.add(loadedMessage);
                    // Update messageDispatchCounter to reflect the highest index loaded for sent messages
                    if (loadedMessage.MESSAGE_INDEX > messageDispatchCounter) {
                        messageDispatchCounter = loadedMessage.MESSAGE_INDEX;
                    }
                } else if (loadedMessage.getMessageStatus().equals("Stored")) {
                    allStoredMessages.add(loadedMessage);
                } else if (loadedMessage.getMessageStatus().equals("Disregarded")) {
                    allDisregardedMessages.add(loadedMessage);
                }
                
                // Add to global ID and Hash lists if not already present
                if (!allMessageIDs.contains(loadedMessage.MESSAGE_ID)) {
                    allMessageIDs.add(loadedMessage.MESSAGE_ID);
                }
                if (!loadedMessage.MESSAGE_HASH.isEmpty() && !allMessageHashes.contains(loadedMessage.MESSAGE_HASH)) {
                    allMessageHashes.add(loadedMessage.MESSAGE_HASH);
                }

            } catch (IOException | ParseException e) {
                // No console output
            }
        }
        // No console output
    }

    /**
     * Displays the sender and recipient of all sent messages.
     * The sender is assumed to be the currently logged-in user.
     * @return A formatted string listing sender and recipient of all sent messages.
     */
    public static String displayAllSentMessagesInfo() {
        if (allSentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }

        StringBuilder sb = new StringBuilder("--- All Sent Messages ---\n");
        for (MessageFeature msg : allSentMessages) {
            sb.append("Sender: ").append(loggedInUsername.isEmpty() ? "Unknown" : loggedInUsername)
              .append(", Recipient: ").append(msg.getMessageRecipient()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Finds and returns the payload of the longest message among all sent and stored messages.
     * This method considers messages from both the 'Sent' and 'Stored' categories to find the longest payload,
     * as implied by the POE's expected output for Test Data Message 2.
     * @return The payload string of the longest message, or a message if no messages are found.
     */
    public static String findLongestSentMessage() {
        // Collect all relevant messages (sent and stored)
        ArrayList<MessageFeature> allRelevantMessages = new ArrayList<>();
        allRelevantMessages.addAll(allSentMessages);
        
        // Add messages from allStoredMessages, but avoid duplicates if a message is already in allSentMessages
        for (MessageFeature storedMsg : allStoredMessages) {
            boolean isDuplicate = false;
            for (MessageFeature sentMsg : allSentMessages) {
                if (storedMsg.getMessageID().equals(sentMsg.getMessageID())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                allRelevantMessages.add(storedMsg);
            }
        }

        if (allRelevantMessages.isEmpty()) {
            return "No messages have been sent or stored to determine the longest.";
        }

        String longestMessagePayload = "";
        int maxLength = 0;

        for (MessageFeature msg : allRelevantMessages) {
            if (msg.MESSAGE_PAYLOAD != null && msg.MESSAGE_PAYLOAD.length() > maxLength) {
                maxLength = msg.MESSAGE_PAYLOAD.length();
                longestMessagePayload = msg.MESSAGE_PAYLOAD;
            }
        }
        return longestMessagePayload;
    }

    /**
     * Searches for a message by its ID across all sent and stored messages.
     * @param searchID The Message ID to search for.
     * @return A formatted string with message details if found, or a "not found" message.
     */
    public static String searchMessageByID(String searchID) {
        if (searchID == null || searchID.trim().isEmpty()) {
            return "Please provide a message ID to search.";
        }
        
        // Search in sent messages
        for (MessageFeature msg : allSentMessages) {
            if (msg.getMessageID().equals(searchID)) {
                return "Message Found (Sent):\n" +
                       "Recipient: " + msg.getMessageRecipient() + "\n" +
                       "Message: \"" + msg.getMessagePayload() + "\"";
            }
        }

        // Search in stored messages
        for (MessageFeature msg : allStoredMessages) {
            if (msg.getMessageID().equals(searchID)) {
                return "Message Found (Stored):\n" +
                       "Recipient: " + msg.getMessageRecipient() + "\n" +
                       "Message: \"" + msg.getMessagePayload() + "\"";
            }
        }

        return "No message found with ID: " + searchID;
    }

    /**
     * Searches for all messages (sent or stored) associated with a particular recipient.
     * @param searchRecipient The recipient's cell number to search for.
     * @return A formatted string listing all messages found for the recipient.
     */
    public static String searchMessagesByRecipient(String searchRecipient) {
        if (searchRecipient == null || searchRecipient.trim().isEmpty()) {
            return "Please provide a recipient number to search.";
        }

        StringBuilder sb = new StringBuilder("--- Messages for Recipient: ").append(searchRecipient).append(" ---\n");
        boolean found = false;

        // Collect messages to avoid concurrent modification if items were removed
        ArrayList<MessageFeature> messagesForRecipient = new ArrayList<>();

        // Search in sent messages
        for (MessageFeature msg : allSentMessages) {
            if (msg.getMessageRecipient().equals(searchRecipient)) {
                messagesForRecipient.add(msg);
                found = true;
            }
        }

        // Search in stored messages, avoiding duplicates that are already in sent list (if a message was sent AND stored)
        for (MessageFeature msg : allStoredMessages) {
            if (msg.getMessageRecipient().equals(searchRecipient)) {
                boolean isDuplicate = false;
                for (MessageFeature existingMsg : messagesForRecipient) {
                    if (existingMsg.getMessageID().equals(msg.getMessageID())) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    messagesForRecipient.add(msg);
                    found = true;
                }
            }
        }
        
        if (!found) {
            return "No messages found for recipient: " + searchRecipient;
        }
        
        // Append collected messages to StringBuilder for output, using their actual status
        for (MessageFeature msg : messagesForRecipient) {
            sb.append(msg.getMessageStatus()).append(": \"").append(msg.getMessagePayload()).append("\"\n");
        }

        return sb.toString();
    }

    /**
     * Deletes a message using its message hash. Removes it from relevant lists
     * and deletes the corresponding JSON file if it exists.
     * @param hashToDelete The message hash to search and delete.
     * @return A status message indicating success or failure.
     */
    public static String deleteMessageByHash(String hashToDelete) {
        if (hashToDelete == null || hashToDelete.trim().isEmpty()) {
            return "Please provide a message hash to delete.";
        }

        boolean foundAndDeleted = false;
        String deletedMessagePayload = "";
        String deletedMessageID = "";
        File fileToDelete = null;

        // Iterate and remove from all lists where the message might exist
        // Check sent messages
        Iterator<MessageFeature> sentIt = allSentMessages.iterator();
        while (sentIt.hasNext()) {
            MessageFeature msg = sentIt.next();
            if (msg.getMessageHash().equals(hashToDelete)) {
                deletedMessagePayload = msg.getMessagePayload();
                deletedMessageID = msg.getMessageID();
                sentIt.remove();
                foundAndDeleted = true;
                break; 
            }
        }

        // Check stored messages
        Iterator<MessageFeature> storedIt = allStoredMessages.iterator();
        while (storedIt.hasNext()) {
            MessageFeature msg = storedIt.next();
            if (msg.getMessageHash().equals(hashToDelete)) {
                deletedMessagePayload = msg.getMessagePayload();
                deletedMessageID = msg.getMessageID();
                storedIt.remove();
                foundAndDeleted = true;
                // Identify the file to delete
                String fileName;
                if (msg.MESSAGE_INDEX == 0) { // Drafts
                    fileName = "message_draft_" + msg.MESSAGE_ID + ".json";
                } else { // Sent and then stored
                    fileName = "message_" + msg.MESSAGE_INDEX + ".json";
                }
                fileToDelete = new File(fileName);
                break;
            }
        }
        
        // Check disregarded messages
        Iterator<MessageFeature> disregardedIt = allDisregardedMessages.iterator();
        while (disregardedIt.hasNext()) {
            MessageFeature msg = disregardedIt.next();
            if (msg.getMessageHash().equals(hashToDelete)) {
                deletedMessagePayload = msg.getMessagePayload();
                deletedMessageID = msg.getMessageID();
                disregardedIt.remove();
                foundAndDeleted = true;
                break;
            }
        }

        // Also remove from global ID and Hash lists if it was found
        if (foundAndDeleted) {
            allMessageIDs.remove(deletedMessageID);
            allMessageHashes.remove(hashToDelete);

            // If a file was identified for deletion (from stored messages), attempt to delete it
            if (fileToDelete != null && fileToDelete.exists()) {
                fileToDelete.delete(); // No console output
            }
            return "Message \"" + deletedMessagePayload + "\" successfully deleted.";
        } else {
            return "Message with hash " + hashToDelete + " not found.";
        }
    }

    /**
     * Generates a comprehensive report of all sent messages, including their hash,
     * recipient, and payload.
     * @return A formatted string report.
     */
    public static String generateSentMessagesReport() {
        if (allSentMessages.isEmpty()) {
            return "No sent messages to report.";
        }

        StringBuilder sb = new StringBuilder("--- QuickChat Sent Messages Report ---\n\n");
        for (int i = 0; i < allSentMessages.size(); i++) {
            MessageFeature msg = allSentMessages.get(i);
            sb.append("Message #").append(i + 1).append(":\n");
            sb.append("  Hash: ").append(msg.getMessageHash()).append("\n");
            sb.append("  Recipient: ").append(msg.getMessageRecipient()).append("\n");
            sb.append("  Message: \"").append(msg.getMessagePayload()).append("\"\n\n");
        }
        return sb.toString();
    }
    
    /**
     * Returns a list of all message IDs stored or sent.
     * @return An ArrayList of strings containing all unique message IDs.
     */
    public static ArrayList<String> getAllMessageIDs() {
        return new ArrayList<>(allMessageIDs); // Return a copy to prevent external modification
    }

    /**
     * Returns a list of all message hashes stored or sent.
     * @return An ArrayList of strings containing all unique message hashes.
     */
    public static ArrayList<String> getAllMessageHashes() {
        return new ArrayList<>(allMessageHashes); // Return a copy
    }
    
    /**
     * Returns the list of all sent messages (for testing/internal use).
     * @return The ArrayList of MessageFeature objects that were sent.
     */
    public static ArrayList<MessageFeature> getSentMessagesForTesting() {
        return allSentMessages;
    }

     /**
     * Returns the list of all stored messages (for testing/internal use).
     * @return The ArrayList of MessageFeature objects that were stored.
     */
    public static ArrayList<MessageFeature> getStoredMessagesForTesting() {
        return allStoredMessages;
    }
    
     /**
     * Returns the list of all disregarded messages (for testing/internal use).
     * @return The ArrayList of MessageFeature objects that were disregarded.
     */
    public static ArrayList<MessageFeature> getDisregardedMessagesForTesting() {
        return allDisregardedMessages;
    }
}
