package st10457602;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir; // Still needed for other tests that might create files, e.g., storeMessage tests

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; // Added import for Paths
import java.util.ArrayList;
import java.util.Iterator; // Explicitly import Iterator

import static org.junit.jupiter.api.Assertions.*;
import org.json.simple.JSONObject; // Added import for JSONObject
import java.io.FileWriter; // Added import for FileWriter

/**
 * Unit test class for the MessageFeature class in the QuickChat application.
 * This class tests the core messaging functionalities including validation of message properties,
 * message hash creation, the message sending process, message storage, and Part 3 features
 * like report generation and message management (search, delete).
 * All tests are designed using JUnit 5 and strictly adhere to the POE's specified test data and units.
 *
 * @author Angela
 */
public class MessageFeatureTest {

    // Test data based on project specifications
    private final String validRecipient1 = "+27718693002";
    private final String messagePayload1 = "Hi Mike, can you join us for dinner tonight"; // Length: 44

    private final String invalidRecipient2 = "08575975889"; // Invalid format (missing +27 prefix)
    private final String messagePayload2 = "Hi Keegan, did you receive the payment?"; // Length: 39

    // Message objects for Part 3 test data
    private MessageFeature message1_sent;
    private MessageFeature message2_stored; // This is a message that is only stored (draft)
    private MessageFeature message3_disregarded;
    private MessageFeature message4_sent;
    private MessageFeature message5_stored; // This is another message that is only stored (draft)

    /**
     * Sets up the testing environment before each test method.
     * This method resets the static message counter in the MessageFeature class to ensure
     * test independence, particularly for tests involving message indexing and counting.
     * It also initializes a default MessageFeature object and clears all static message lists.
     */
    @BeforeEach
    public void setUp() {
        MessageFeature.resetMessageCounterForTesting(); // Clear all static lists and reset counter
        
        // Initialize the default message for general tests (though not directly used by Part 3 tests in setUp)
        // This is here to match the structure of the original student code, but for part 3 tests,
        // specific message objects are created below.
        MessageFeature message = new MessageFeature(validRecipient1, messagePayload1);

        // Set logged in username for report testing
        MessageFeature.setLoggedInUsername("Angela Michelle");

        // --- Setup Part 3 Test Data Messages ---
        // These messages are created and processed, populating the static lists in MessageFeature.
        // Message 1: Sent (from POE Test Data Message 1)
        message1_sent = new MessageFeature("+27834557896", "Did you get the cake?");
        message1_sent.sentMessage(); // Mark as sent and populate allSentMessages, allMessageIDs, allMessageHashes
        message1_sent.storeMessage(); // Also store it to file, and adds to allStoredMessages

        // Message 2: Stored (from POE Test Data Message 2)
        // This message is only stored, so its MESSAGE_INDEX will remain 0,
        // but its MESSAGE_HASH will be generated during storeMessage().
        message2_stored = new MessageFeature("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        message2_stored.storeMessage(); // Only store it, adds to allStoredMessages, allMessageIDs, allMessageHashes

        // Message 3: Disregarded (from POE Test Data Message 3)
        message3_disregarded = new MessageFeature("+27834484567", "Yohoooo, I am at your gate.");
        message3_disregarded.disregardMessage(); // Mark as disregarded, adds to allDisregardedMessages

        // Message 4: Sent (from POE Test Data Message 4)
        // Correcting recipient based on validation rules for Message 4 (POE shows '083...', needs '+27')
        message4_sent = new MessageFeature("+27838884567", "It is dinner time!");
        message4_sent.sentMessage(); // Mark as sent and populate lists
        message4_sent.storeMessage(); // Also store it to file

        // Message 5: Stored (from POE Test Data Message 5)
        // This message is only stored, similar to message2_stored.
        message5_stored = new MessageFeature("+27838884567", "Ok, I am leaving without you.");
        message5_stored.storeMessage(); // Only store it
    }

    /**
     * Tests the MessageFeature constructor to ensure it correctly initializes all properties
     * of a new MessageFeature object, including auto-generated ID, recipient, payload,
     * and default values for index, hash, and status.
     */
    @Test
    public void testMessageConstructor_InitializesPropertiesCorrectly() {
        // Reset specific for this test to avoid interference from setUp's global state
        MessageFeature.resetMessageCounterForTesting(); 
        MessageFeature msg = new MessageFeature("testRecipient", "testPayload");
        assertNotNull(msg.getMessageID(), "Message ID should be automatically generated and not null.");
        assertTrue(msg.getMessageID().matches("\\d{10}"), "Message ID should consist of 10 digits.");
        assertEquals("testRecipient", msg.getMessageRecipient(), "Recipient should match the constructor argument.");
        assertEquals("testPayload", msg.getMessagePayload(), "Payload should match the constructor argument.");
        assertEquals(0, msg.getMessageIndex(), "Initial message index should be 0.");
        assertEquals("", msg.getMessageHash(), "Initial message hash should be an empty string.");
        assertEquals("New", msg.getMessageStatus(), "Initial message status should be 'New'.");
    }

    /**
     * Tests the getGeneratedIdNotification method to ensure it returns a string
     * in the correct format, confirming Message ID generation.
     */
    @Test
    public void testGetGeneratedIdNotification_ReturnsCorrectFormat() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature("123", "test");
        String notification = msg.getGeneratedIdNotification();
        assertTrue(notification.startsWith("Message ID generated: "), "Notification string format is incorrect.");
        assertEquals(msg.getMessageID(), notification.substring("Message ID generated: ".length()), "Notification should accurately contain the generated Message ID.");
    }

    // --- Payload Length Validation Tests ---

    /**
     * Tests validatePayloadLength with valid payloads (short and exact max length).
     * Expects the success message "Message ready to send."
     */
    @Test
    public void testValidatePayloadLength_ValidPayload_ReturnsSuccessMessage() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        String shortPayload = "Hello";
        assertEquals("Message ready to send.", msg.validatePayloadLength(shortPayload), "Short payload should be valid.");

        String exactLengthPayload = new String(new char[250]).replace('\0', 'a');
        assertEquals("Message ready to send.", msg.validatePayloadLength(exactLengthPayload), "Payload of exact maximum length (250 chars) should be valid.");
    }

    /**
     * Tests validatePayloadLength with payloads exceeding the maximum length.
     * Expects a failure message indicating the number of excess characters.
     */
    @Test
    public void testValidatePayloadLength_TooLongPayload_ReturnsFailureMessageWithExcessCount() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        String longPayload = new String(new char[251]).replace('\0', 'a'); // 251 chars
        assertEquals("Message exceeds 250 characters by 1, please reduce size.", msg.validatePayloadLength(longPayload), "Payload exceeding max length by 1 char should report correctly.");

        String veryLongPayload = new String(new char[300]).replace('\0', 'b'); // 300 chars
        assertEquals("Message exceeds 250 characters by 50, please reduce size.", msg.validatePayloadLength(veryLongPayload), "Payload exceeding max length by 50 chars should report correctly.");
    }

    /**
     * Tests validatePayloadLength with an empty payload.
     * As per current logic, an empty payload is valid by length check but might be rejected later by sentMessage.
     */
    @Test
    public void testValidatePayloadLength_EmptyPayload_ReturnsSuccessMessage() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        assertEquals("Message ready to send.", msg.validatePayloadLength(""), "Empty payload should be considered valid by length check.");
    }
    
    /**
     * Tests validatePayloadLength with a null payload.
     * Expects a failure message, as implemented in the MessageFeature class.
     */
    @Test
    public void testValidatePayloadLength_NullPayload_ReturnsFailureMessage() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        assertEquals("Message exceeds 250 characters by -250, please reduce size.", msg.validatePayloadLength(null), "Null payload should result in a specific failure message.");
    }

    // --- Recipient Number Validation Tests ---

    /**
     * Tests validateRecipientNumber with a correctly formatted recipient number.
     * Expects the success message "Cell phone number successfully captured."
     */
    @Test
    public void testValidateRecipientNumber_ValidFormat_ReturnsSuccessMessage() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        assertEquals("Cell phone number successfully captured.", msg.validateRecipientNumber("+27123456789"), "Correctly formatted South African number should be valid.");
    }

    /**
     * Tests validateRecipientNumber with various incorrectly formatted recipient numbers.
     * Expects the failure message detailing incorrect format or missing international code.
     */
    @Test
    public void testValidateRecipientNumber_InvalidFormat_ReturnsFailureMessage() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "test");
        String expectedError = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        assertEquals(expectedError, msg.validateRecipientNumber("0712345678"), "Number missing '+27' prefix should be invalid.");
        assertEquals(expectedError, msg.validateRecipientNumber("+2712345678"), "Number too short after '+27' should be invalid.");
        assertEquals(expectedError, msg.validateRecipientNumber("+271234567890"), "Number too long after '+27' should be invalid.");
        assertEquals(expectedError, msg.validateRecipientNumber("invalid"), "Non-numeric or improperly structured number should be invalid.");
        assertEquals(expectedError, msg.validateRecipientNumber(""), "Empty string for recipient number should be invalid.");
        assertEquals(expectedError, msg.validateRecipientNumber(null), "Null recipient number should be invalid.");
    }

    // --- Message Hash Creation Tests ---

    /**
     * Tests createMessageHash with specific input data to verify correct hash generation
     * based on the defined format (FirstTwoID:Index:FirstWordLastWord, all uppercase),
     * ensuring punctuation is removed.
     */
    @Test
    public void testCreateMessageHash_WithSpecifiedData_ReturnsCorrectHash() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        
        // Data for Test Case 1 (Message 1) from specifications:
        // Message: "Hi Mike, can you join us for dinner tonight"
        // Expected Hash (assuming ID starts "00", index 0): "00:0:HITONIGHT"
        // The message ID passed here is a fixed string for predictable hashing in test
        String testID = "0012345678"; 
        int testIndex = 0;
        String testPayload = "Hi Mike, can you join us for dinner tonight";
        
        MessageFeature msg = new MessageFeature(validRecipient1, testPayload); // Actual message object for method
        assertEquals("00:0:HITONIGHT", msg.createMessageHash(testID, testIndex, testPayload), "Hash for Test Case 1 data should be '00:0:HITONIGHT'.");

        // Test case with punctuation in payload to ensure stripping
        String testID2 = "AB98765432";
        int testIndex2 = 15;
        String testPayload2 = " Hello world! example. "; // Note leading/trailing spaces and punctuation
        assertEquals("AB:15:HELLOEXAMPLE", msg.createMessageHash(testID2, testIndex2, testPayload2), "Hash with punctuation in payload should correctly use trimmed and cleaned words.");
    }

    // --- sentMessage() Tests ---

    /**
     * Tests sentMessage with an invalid recipient number.
     * Expects a failure message and ensures message state (index, hash, total count) is not altered.
     */
    @Test
    public void testSentMessage_InvalidRecipient_ReturnsFailure() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(invalidRecipient2, messagePayload1); // Invalid recipient
        assertEquals("Failed to send message: Invalid recipient", msg.sentMessage(), "sentMessage should return failure for an invalid recipient.");
        assertEquals(0, msg.getMessageIndex(), "Message index should remain 0 on a failed send attempt due to invalid recipient.");
        assertTrue(msg.getMessageHash().isEmpty(), "Message hash should remain empty on a failed send attempt.");
        assertEquals(0, MessageFeature.returnTotalMessages(), "Total messages sent counter should remain 0 after a failed send.");
        assertTrue(MessageFeature.getSentMessagesForTesting().isEmpty(), "Sent messages list should be empty on failed send.");
    }

    /**
     * Tests sentMessage with a payload that exceeds the maximum allowed length.
     * Expects a failure message and ensures the total message count is not incremented.
     */
    @Test
    public void testSentMessage_PayloadTooLong_ReturnsFailure() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        String longPayload = new String(new char[251]).replace('\0', 'a');
        MessageFeature msg = new MessageFeature(validRecipient1, longPayload); // Payload too long
        assertEquals("Failed to send message: Payload too long", msg.sentMessage(), "sentMessage should return failure for a payload exceeding maximum length.");
        assertEquals(0, MessageFeature.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is too long.");
        assertTrue(MessageFeature.getSentMessagesForTesting().isEmpty(), "Sent messages list should be empty on failed send.");
    }
    
    /**
     * Tests sentMessage with a payload that is empty or contains only whitespace.
     * Expects a failure message and ensures the total message count is not incremented.
     */
    @Test
    public void testSentMessage_EmptyPayload_ReturnsFailure() {
        // Reset specific for this test
        MessageFeature.resetMessageCounterForTesting();
        MessageFeature msg = new MessageFeature(validRecipient1, "   "); // Payload is effectively empty after trim
        assertEquals("Failed to send message: Message content cannot be empty", msg.sentMessage(), "sentMessage should return failure for an empty payload.");
        assertEquals(0, MessageFeature.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is empty.");
        assertTrue(MessageFeature.getSentMessagesForTesting().isEmpty(), "Sent messages list should be empty on failed send.");
    }
    
    /**
     * Tests successful sentMessage operation.
     * Verifies message index, hash, total count, and addition to sent messages list.
     */
    @Test
    public void testSentMessage_Success() {
        // Reset for a clean single message test
        MessageFeature.resetMessageCounterForTesting(); 
        MessageFeature msg = new MessageFeature("+27721234567", "Hello World!");
        // The expected hash should NOT include punctuation due to the updated createMessageHash logic
        String expectedHash = msg.getMessageID().substring(0, 2) + ":1:HELLOWORLD"; 
        
        String result = msg.sentMessage();
        assertEquals("Message successfully sent.", result, "Message should be sent successfully.");
        assertEquals(1, msg.getMessageIndex(), "Message index should be 1 after first sent message.");
        assertEquals(expectedHash.toUpperCase(), msg.getMessageHash(), "Message hash should be correctly generated.");
        assertEquals(1, MessageFeature.returnTotalMessages(), "Total messages sent counter should increment.");
        assertEquals(1, MessageFeature.getSentMessagesForTesting().size(), "Sent messages list should contain 1 message.");
        assertEquals(msg, MessageFeature.getSentMessagesForTesting().get(0), "The sent message object should be added to the list.");
        assertTrue(MessageFeature.getAllMessageIDs().contains(msg.getMessageID()), "Message ID should be in global IDs list.");
        assertTrue(MessageFeature.getAllMessageHashes().contains(msg.getMessageHash()), "Message Hash should be in global hashes list.");
        assertEquals("Sent", msg.getMessageStatus(), "Message status should be 'Sent'.");
    }


    // --- storeMessage() Tests ---

    /**
     * Tests storeMessage for a draft message (not yet sent, index 0).
     * Verifies that a JSON file with a draft-specific name is created and contains
     * the correct message details (ID, index 0, non-empty hash, payload) and 'Stored' status.
     * Uses @TempDir for managing temporary file creation if needed. Test cleans up the file.
     */
    @Test
    public void testStoreMessage_Draft_CreatesFileWithIndex0(@TempDir Path tempDir) throws IOException {
        // Ensure clean state for this test only
        MessageFeature.resetMessageCounterForTesting();
        
        MessageFeature draftMessage = new MessageFeature(validRecipient1, "This is a draft.");
        
        // Call the actual storeMessage to create the file and update in-memory lists.
        String result = draftMessage.storeMessage(); 
        
        // The file is created in the current working directory, so we refer to it directly.
        String expectedFileName = "message_draft_" + draftMessage.getMessageID() + ".json";
        File createdFile = new File(expectedFileName);

        assertEquals("Message successfully stored.", result, "storeMessage should return success for a draft.");
        assertTrue(createdFile.exists(), "JSON file for the draft message should be created.");
        
        String content = Files.readString(createdFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + draftMessage.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":0"), "Stored JSON for a draft should have MESSAGE_INDEX as 0.");
        assertFalse(content.contains("\"MESSAGE_HASH\":\"\""), "Stored JSON for a draft should have a non-empty MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a draft.\""), "Stored JSON should contain the correct payload.");
        assertTrue(content.contains("\"MESSAGE_STATUS\":\"Stored\""), "Stored JSON should have MESSAGE_STATUS as 'Stored'.");
        assertEquals(1, MessageFeature.getStoredMessagesForTesting().size(), "Stored messages list should contain one draft message.");
        
        assertTrue(createdFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    /**
     * Tests storeMessage for a message that has been successfully sent.
     * Verifies that a JSON file (e.g., "message_1.json") is created with the correct
     * message index, populated hash, and preserves its 'Sent' status.
     * Test cleans up the file.
     */
    @Test
    public void testStoreMessage_SentMessage_CreatesFileWithCorrectIndexAndHash(@TempDir Path tempDir) throws IOException {
        // Ensure clean state for this test only
        MessageFeature.resetMessageCounterForTesting();
        
        MessageFeature sentMsg = new MessageFeature(validRecipient1, "This is a sent message.");
        sentMsg.sentMessage(); // Process the message as sent, sets status to "Sent"

        assertNotEquals(0, sentMsg.getMessageIndex(), "Index of a sent message should not be 0.");
        assertFalse(sentMsg.getMessageHash().isEmpty(), "Hash of a sent message should not be empty.");

        // Call the actual storeMessage to create the file and update in-memory lists.
        String storeResult = sentMsg.storeMessage(); 
        
        // The file is created in the current working directory, so we refer to it directly.
        String expectedFileName = "message_" + sentMsg.getMessageIndex() + ".json";
        File createdFile = new File(expectedFileName);
        
        assertEquals("Message successfully stored.", storeResult, "storeMessage should return success for a sent message.");
        assertTrue(createdFile.exists(), "JSON file for the sent message should be created.");

        String content = Files.readString(createdFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + sentMsg.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":" + sentMsg.getMessageIndex()), "Stored JSON should contain the correct MESSAGE_INDEX.");
        assertTrue(content.contains("\"MESSAGE_HASH\":\"" + sentMsg.getMessageHash() + "\""), "Stored JSON should contain the correct MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a sent message.\""), "Stored JSON should contain the correct payload.");
        assertTrue(content.contains("\"MESSAGE_STATUS\":\"Sent\""), "Stored JSON should have MESSAGE_STATUS as 'Sent'."); // Adjusted expectation
        
        // This message should be in both sent and stored lists now if storeMessage adds to both
        assertEquals(1, MessageFeature.getStoredMessagesForTesting().size(), "Stored messages list should contain this sent and stored message.");
        assertTrue(MessageFeature.getSentMessagesForTesting().contains(sentMsg), "Sent messages list should still contain the sent message.");
        
        assertTrue(createdFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    // --- returnTotalMessages() Tests (basic cases) ---

    /**
     * Tests returnTotalMessages when no messages have been sent.
     * Expects the counter to be 0.
     */
    @Test
    public void testReturnTotalMessages_NoMessagesSent_ReturnsZero() {
        MessageFeature.resetMessageCounterForTesting(); // Ensure clean slate
        assertEquals(0, MessageFeature.returnTotalMessages(), "returnTotalMessages should return 0 initially.");
    }
    
    /**
     * Tests returnTotalMessages after multiple messages have been sent.
     */
    @Test
    public void testReturnTotalMessages_MultipleMessagesSent_ReturnsCorrectCount() {
        // setUp already sends message1_sent and message4_sent
        assertEquals(2, MessageFeature.returnTotalMessages(), "returnTotalMessages should return 2 after two messages are sent in setup.");
    }
    
    // --- New Part 3 Unit Tests ---

    /**
     * Tests that the sent messages array is correctly populated.
     * Uses test data message 1 and 4 from POE.
     */
    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // Messages 1 and 4 are marked as sent in setUp.
        ArrayList<MessageFeature> sentMessages = MessageFeature.getSentMessagesForTesting();
        assertEquals(2, sentMessages.size(), "Sent messages array should contain 2 messages.");
        
        assertTrue(sentMessages.contains(message1_sent), "Sent messages array should contain message1_sent.");
        assertTrue(sentMessages.contains(message4_sent), "Sent messages array should contain message4_sent.");
        
        // Verify payloads from the POE test data
        ArrayList<String> sentPayloads = new ArrayList<>();
        for (MessageFeature msg : sentMessages) {
            sentPayloads.add(msg.getMessagePayload());
        }
        
        assertTrue(sentPayloads.contains("Did you get the cake?"), "Sent payloads should include 'Did you get the cake?'.");
        assertTrue(sentPayloads.contains("It is dinner time!"), "Sent payloads should include 'It is dinner time!'.");
    }
    
    /**
     * Tests the displayAllSentMessagesInfo method for correct output.
     */
    @Test
    public void testDisplayAllSentMessagesInfo() {
        String expectedOutput = "--- All Sent Messages ---\n" +
                                "Sender: Angela Michelle, Recipient: +27834557896\n" +
                                "Sender: Angela Michelle, Recipient: +27838884567\n";
        
        assertEquals(expectedOutput, MessageFeature.displayAllSentMessagesInfo(), "Display all sent messages info should match expected format.");
    }

    /**
     * Tests finding the longest sent message.
     * Based on POE test data, the longest message is from Message 2, which is 'Stored'.
     * The method `findLongestSentMessage` now considers both sent and stored messages
     * to match the POE's expected output for this test case.
     */
    @Test
    public void testFindLongestSentMessage() {
        // Message 1 (sent): "Did you get the cake?" (22 chars)
        // Message 2 (stored): "Where are you? You are late! I have asked you to be on time." (59 chars)
        // Message 3 (disregarded): "Yohoooo, I am at your gate." (26 chars)
        // Message 4 (sent): "It is dinner time!" (18 chars)
        // Message 5 (stored): "Ok, I am leaving without you." (29 chars)

        // As per POE, expected longest message is from Test Data Message 2
        String expectedLongest = "Where are you? You are late! I have asked you to be on time.";
        
        assertEquals(expectedLongest, MessageFeature.findLongestSentMessage(), "The longest message should be the one from Test Data Message 2.");
    }

    /**
     * Tests searching for a message by its ID.
     * Uses test data message 4 from POE.
     */
    @Test
    public void testSearchMessageByID() {
        // Message 4 details: ID (auto-generated), Recipient: +27838884567, Message: "It is dinner time!"
        // Since ID is auto-generated, we need to get the actual ID from message4_sent
        String message4ID = message4_sent.getMessageID();
        String expectedResult = "Message Found (Sent):\n" +
                                "Recipient: +27838884567\n" +
                                "Message: \"It is dinner time!\"";
        
        assertEquals(expectedResult, MessageFeature.searchMessageByID(message4ID), "Searching for message ID of message 4 should return its details.");

        // Test for a non-existent ID
        String notFoundResult = MessageFeature.searchMessageByID("9999999999");
        assertEquals("No message found with ID: 9999999999", notFoundResult, "Searching for non-existent ID should return 'not found'.");
    }

    /**
     * Tests searching for all messages sent to a particular recipient.
     * Uses test data recipient +27838884567 from POE.
     */
    @Test
    public void testSearchMessagesByRecipient() {
        String recipient = "+27838884567";
        // The expected result should reflect the actual messages setup in @BeforeEach.
        // Message 4 (sent): "It is dinner time!"
        // Message 2 (stored): "Where are you? You are late! I have asked you to be on time."
        // Message 5 (stored): "Ok, I am leaving without you."
        String expectedResult = "--- Messages for Recipient: +27838884567 ---\n" +
                                "Sent: \"It is dinner time!\"\n" +
                                "Stored: \"Where are you? You are late! I have asked you to be on time.\"\n" +
                                "Stored: \"Ok, I am leaving without you.\"\n";
        
        assertEquals(expectedResult, MessageFeature.searchMessagesByRecipient(recipient), "Searching messages by recipient should return all messages for that recipient.");

        // Test for a recipient with no messages
        String noMessagesResult = MessageFeature.searchMessagesByRecipient("+27111111111");
        assertEquals("No messages found for recipient: +27111111111", noMessagesResult, "Searching for recipient with no messages should return 'no messages found'.");
    }

    /**
     * Tests deleting a message using its message hash.
     * Uses test data message 2 from POE.
     */
    @Test
    public void testDeleteMessageByHash() {
        // Hash for message2_stored - this will now be populated due to changes in storeMessage()
        String hashToDelete = message2_stored.getMessageHash();
        String expectedPayload = message2_stored.getMessagePayload();
        
        // Ensure initial state is correctly populated by setUp
        assertTrue(MessageFeature.getStoredMessagesForTesting().contains(message2_stored), "Message 2 should be in stored list before deletion.");
        
        // File existence check: use the correct filename pattern for drafts
        // The file is created in CWD by storeMessage, so check there.
        File message2File = new File("message_draft_" + message2_stored.getMessageID() + ".json");
        assertTrue(message2File.exists(), "Message 2 JSON file should exist before deletion.");

        String result = MessageFeature.deleteMessageByHash(hashToDelete);
        assertEquals("Message \"" + expectedPayload + "\" successfully deleted.", result, "Deletion result message should be correct.");
        
        // Verify message is no longer in the list
        assertFalse(MessageFeature.getStoredMessagesForTesting().contains(message2_stored), "Message 2 should be removed from stored list after deletion.");
        assertFalse(MessageFeature.getAllMessageHashes().contains(hashToDelete), "Hash should be removed from global hash list.");
        assertFalse(MessageFeature.getAllMessageIDs().contains(message2_stored.getMessageID()), "ID should be removed from global ID list.");
        
        // Verify JSON file is deleted
        assertFalse(message2File.exists(), "Message 2 JSON file should be deleted.");
        
        // Test deleting a non-existent hash
        String nonExistentHashResult = MessageFeature.deleteMessageByHash("NONEXISTENTHASH");
        assertEquals("Message with hash NONEXISTENTHASH not found.", nonExistentHashResult, "Attempting to delete non-existent hash should return 'not found'.");
    }

    /**
     * Tests the generation of the full sent messages report.
     */
    @Test
    public void testGenerateSentMessagesReport() {
        // setUp already populates with message1_sent and message4_sent
        String expectedReport = "--- QuickChat Sent Messages Report ---\n\n" +
                                "Message #1:\n" +
                                "  Hash: " + message1_sent.getMessageHash() + "\n" +
                                "  Recipient: +27834557896\n" +
                                "  Message: \"Did you get the cake?\"\n\n" +
                                "Message #2:\n" +
                                "  Hash: " + message4_sent.getMessageHash() + "\n" +
                                "  Recipient: +27838884567\n" +
                                "  Message: \"It is dinner time!\"\n\n";
        
        assertEquals(expectedReport, MessageFeature.generateSentMessagesReport(), "The generated report should match the expected format and content.");

        // Test with no sent messages
        MessageFeature.resetMessageCounterForTesting();
        assertEquals("No sent messages to report.", MessageFeature.generateSentMessagesReport(), "Report should indicate no messages if list is empty.");
    }
}
