package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.ChatMessage;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for ChatMessage entities.
 * Provides methods for CRUD operations and specific queries related to chat messages.
 */
public class ChatMessageDao {
    /**
     * Persists a new chat message entity directly.
     *
     * @param message The ChatMessage entity to create.
     */
    public void create(ChatMessage message) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(message);
    }
    
    /**
     * Creates and persists a new chat message from individual parameters.
     *
     * @param senderUsername The username of the user sending the message.
     * @param recipientUsername The username of the user receiving the message.
     * @param content The text content of the message.
     * @param timestamp The date and time when the message was sent.
     * @return The newly created and persisted ChatMessage entity.
     */
    public ChatMessage createMessage(String senderUsername, String recipientUsername, String content, Date timestamp) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        ChatMessage newMessage = new ChatMessage();
        newMessage.setSenderUsername(senderUsername);
        newMessage.setRecipientUsername(recipientUsername);
        newMessage.setContent(content);
        
        if (timestamp != null) {
            newMessage.setTimestamp(timestamp);
        }
        
        em.persist(newMessage);
        return newMessage;
    }
    
    /**
     * Retrieves a specific chat message by its unique ID.
     *
     * @param id The UUID string of the message.
     * @return The found ChatMessage, or null if not found.
     */
    public ChatMessage getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(ChatMessage.class, id);
    }
    
    /**
     * Finds all messages exchanged between two specific users, ordered by timestamp.
     *
     * @param user1Username Username of the first user in the conversation.
     * @param user2Username Username of the second user in the conversation.
     * @param ascending If true, sort by timestamp ascending (oldest first).
     * @return A list of ChatMessage objects representing the conversation.
     */
    public List<ChatMessage> findByConversation(String user1Username, String user2Username, boolean ascending) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        String sortOrder = ascending ? "ASC" : "DESC";
        String jpql = "SELECT m FROM ChatMessage m WHERE " +
                "(m.senderUsername = :user1 AND m.recipientUsername = :user2) OR " +
                "(m.senderUsername = :user2 AND m.recipientUsername = :user1) " +
                "ORDER BY m.timestamp " + sortOrder;
                
        TypedQuery<ChatMessage> query = em.createQuery(jpql, ChatMessage.class);
        query.setParameter("user1", user1Username);
        query.setParameter("user2", user2Username);
        
        return query.getResultList();
    }
    
    /**
     * Finds all messages exchanged between two specific users, ordered ascending by timestamp.
     *
     * @param user1Username Username of the first user.
     * @param user2Username Username of the second user.
     * @return A list of ChatMessage objects ordered oldest to newest.
     */
    public List<ChatMessage> findConversationAscending(String user1Username, String user2Username) {
        return findByConversation(user1Username, user2Username, true);
    }
    
    /**
     * Updates an existing chat message in the database.
     *
     * @param message The ChatMessage entity with updated information.
     * @return The updated, managed ChatMessage entity.
     */
    public ChatMessage update(ChatMessage message) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.merge(message);
    }
    
    /**
     * Deletes a chat message from the database.
     *
     * @param message The ChatMessage entity to delete.
     */
    public void delete(ChatMessage message) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        if (em.contains(message)) {
            em.remove(message);
        } else {
            ChatMessage managedMessage = getById(message.getId());
            if (managedMessage != null) {
                em.remove(managedMessage);
            }
        }
    }
    
    /**
     * Deletes a chat message by its ID.
     *
     * @param id The ID of the message to delete.
     */
    public void delete(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        ChatMessage message = getById(id);
        if (message != null) {
            em.remove(message);
        }
    }
}