package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.ChatMessageDao;
import com.sismics.docs.core.model.jpa.ChatMessage;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * REST Resource for Chat Messages.
 * Provides endpoints for retrieving conversations and sending messages.
 */
@Path("/chat")
public class ChatMessageResource extends BaseResource {
    
    // Helper to format dates as ISO 8601 String
    private String formatTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        // Use ISO 8601 format (UTC)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
    
    /**
     * Get the conversation history between two users.
     *
     * @param user1Username Username of the first participant
     * @param user2Username Username of the second participant
     * @param sortOrder Sort order ("asc" or "desc")
     * @return Response containing the list of messages
     */
    @GET
    @Path("/conversation/{user1}/{user2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConversation(
            @PathParam("user1") String user1Username,
            @PathParam("user2") String user2Username,
            @QueryParam("sort") @DefaultValue("asc") String sortOrder) {
        
        // TODO: Add proper authentication and authorization checks
        
        // Determine sort order
        boolean ascending = !"desc".equalsIgnoreCase(sortOrder);
        
        // Fetch data from DAO
        ChatMessageDao chatMessageDao = new ChatMessageDao();
        List<ChatMessage> messages = chatMessageDao.findByConversation(user1Username, user2Username, ascending);
        
        // Build JSON response
        JsonArrayBuilder messagesArray = Json.createArrayBuilder();
        for (ChatMessage msg : messages) {
            messagesArray.add(Json.createObjectBuilder()
                    .add("id", msg.getId())
                    .add("senderUsername", msg.getSenderUsername())
                    .add("recipientUsername", msg.getRecipientUsername())
                    .add("content", msg.getContent() != null ? msg.getContent() : "")
                    .add("timestamp", formatTimestamp(msg.getTimestamp())));
        }
        JsonObject response = Json.createObjectBuilder()
                .add("messages", messagesArray)
                .build();
        
        return Response.ok().entity(response).build();
    }
    
    /**
     * Send a new chat message.
     *
     * @param senderUsername The username claiming to send the message
     * @param recipientUsername The username to send the message to
     * @param content The message text
     * @return Response containing the newly created message object
     */
    @POST
    @Path("/messages")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(
            @FormParam("sender") String senderUsername,
            @FormParam("recipient") String recipientUsername,
            @FormParam("content") String content) {
        
        // TODO: Add proper authentication and get sender from authenticated user
        
        // Prepare message data
        Date timestamp = new Date();
        
        // Basic input validation
        if (senderUsername == null || senderUsername.trim().isEmpty() || 
            recipientUsername == null || recipientUsername.trim().isEmpty() || 
            content == null) {
            throw new BadRequestException("Sender, recipient, and content parameters are required");
        }
        
        // Use DAO to create the message
        ChatMessageDao chatMessageDao = new ChatMessageDao();
        ChatMessage newMessage;
        try {
            newMessage = chatMessageDao.createMessage(
                    senderUsername.trim(), 
                    recipientUsername.trim(), 
                    content, 
                    timestamp);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to save message", e);
        }
        
        // Build the response object
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("id", newMessage.getId())
                .add("senderUsername", newMessage.getSenderUsername())
                .add("recipientUsername", newMessage.getRecipientUsername())
                .add("content", newMessage.getContent() != null ? newMessage.getContent() : "")
                .add("timestamp", formatTimestamp(newMessage.getTimestamp()));
        
        return Response.ok().entity(responseBuilder.build()).build();
    }
}