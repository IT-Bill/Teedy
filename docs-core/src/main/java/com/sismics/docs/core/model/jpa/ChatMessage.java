package com.sismics.docs.core.model.jpa;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a single chat message stored in the database.
 */
@Entity
@Table(name = "T_CHAT_MESSAGE")
public class ChatMessage {
    /**
     * Primary key (UUID).
     */
    @Id
    @Column(name = "MSG_ID_C", length = 36)
    private String id;
    
    /**
     * Timestamp when the message was created/sent.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MSG_TIMESTAMP_D", nullable = false)
    private Date timestamp;
    
    /**
     * Username of the user who sent the message.
     */
    @Column(name = "MSG_SENDER_USERNAME_C", nullable = false, length = 50)
    private String senderUsername;
    
    /**
     * Username of the user who received the message.
     */
    @Column(name = "MSG_RECIPIENT_USERNAME_C", nullable = false, length = 50)
    private String recipientUsername;
    
    /**
     * The actual content of the chat message.
     */
    @Column(name = "MSG_CONTENT_C", columnDefinition = "TEXT")
    private String content;
    
    /**
     * Default constructor.
     * Generates a unique ID and sets the current timestamp upon creation.
     */
    public ChatMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = new Date();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getTimestamp() {
        return timestamp == null ? null : new Date(timestamp.getTime());
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp == null ? null : new Date(timestamp.getTime());
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getRecipientUsername() {
        return recipientUsername;
    }
    
    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", senderUsername='" + senderUsername + '\'' +
                ", recipientUsername='" + recipientUsername + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 30)) + "..." : "null") + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}