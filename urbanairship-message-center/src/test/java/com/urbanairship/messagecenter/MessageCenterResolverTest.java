/* Copyright Airship and Contributors */

package com.urbanairship.messagecenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MessageCenterResolverTest {

    private MessageCenterResolver resolver;

    @Before
    public void setUp() {
        resolver = new MessageCenterResolver(ApplicationProvider.getApplicationContext());

        // Populate the MCRAP database with 10 messages
        for (int i = 0; i < 10; i++) {
            MessageCenterTestUtils.insertMessage(String.valueOf(i + 1) + "_message_id");
        }
    }

    /**
     * Test get messages returns all the messages.
     */
    @Test
    public void testGetMessages() {
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     * Test marking messages as read.
     */
    @Test
    public void testMarkMessagesRead() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesRead(keys);
        assertEquals(keys.size(), updated);

        for (Message message : resolver.getMessages()) {
            if (!message.isRead()) {
                continue;
            }

            if (keys.contains(message.getMessageId())) {
                keys.remove(message.getMessageId());
            } else {
                fail("Unexpected message read: " + message);
            }
        }

        assertEquals(0, keys.size());
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     * Test marking messages as read from the origin.
     */
    @Test
    public void testMarkMessagesReadOrigin() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesReadOrigin(keys);
        assertEquals(keys.size(), updated);
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     * Test marking messages as unread.
     */
    @Test
    public void testMarkMessagesUnread() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesRead(keys);
        assertEquals(keys.size(), updated);

        updated = resolver.markMessagesUnread(keys);
        assertEquals(keys.size(), updated);

        for (Message message : resolver.getMessages()) {
            assertFalse(message.isRead());
        }
    }

    /**
     * Test marking messages for deletion.
     */
    @Test
    public void testMarkMessagesDeleted() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesDeleted(keys);
        assertEquals(keys.size(), updated);

        for (Message message : resolver.getMessages()) {
            if (!message.isDeleted()) {
                continue;
            }

            if (keys.contains(message.getMessageId())) {
                keys.remove(message.getMessageId());
            } else {
                fail("Unexpected message marked for deletion: " + message);
            }
        }

        assertEquals(0, keys.size());
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     * Test deleting messages.
     */
    @Test
    public void testDeleteMessages() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int deleted = resolver.deleteMessages(keys);
        assertEquals(keys.size(), deleted);

        for (Message message : resolver.getMessages()) {
            assertFalse(keys.contains(message.getMessageId()));
        }

        assertEquals(7, resolver.getMessages().size());
    }

    /**
     * Test getting the messages IDs that have been marked for deletion.
     */
    @Test
    public void testGetDeletedMessageIds() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesDeleted(keys);
        assertEquals(keys.size(), updated);

        assertEquals(keys, messageIdsFromMessages(resolver.getLocallyDeletedMessages()));
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     * Test getting the message IDs of messages that are unread on the client but not the
     * origin.
     */
    @Test
    public void testGetUnreadMessageIds() {
        Set<String> keys = new HashSet<>();
        keys.add("1_message_id");
        keys.add("2_message_id");
        keys.add("6_message_id");

        int updated = resolver.markMessagesRead(keys);
        assertEquals(keys.size(), updated);

        assertEquals(keys, messageIdsFromMessages(resolver.getLocallyReadMessages()));
        assertEquals(10, resolver.getMessages().size());
    }

    /**
     *  Test deleting all messages.
     */
    @Test
    public void testDeleteAllMessages() {
        int deleted = resolver.deleteAllMessages();
        assertEquals(10, deleted);
        assertEquals(0, resolver.getMessages().size());
    }


    private Set<String> messageIdsFromMessages(Collection<Message> messages) {
        Set<String> ids = new HashSet<>();
        for (Message message : messages) {
            ids.add(message.getMessageId());
        }
        return ids;
    }

}
