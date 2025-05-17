package com.sismics.docs.core.dao.dto;

/**
 * Registration request DTO.
 *
 * @author Copilot
 */
public class RegistrationRequestDto {
    /**
     * Registration request ID.
     */
    private String id;
    
    /**
     * Username.
     */
    private String username;
    
    /**
     * Email address.
     */
    private String email;
    
    /**
     * Creation timestamp.
     */
    private Long createTimestamp;
    
    /**
     * Request status.
     */
    private String status;
    
    /**
     * Rejection reason.
     */
    private String rejectionReason;
    
    /**
     * Processing user ID.
     */
    private String userId;
    
    /**
     * Processing timestamp.
     */
    private Long processTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getProcessTimestamp() {
        return processTimestamp;
    }

    public void setProcessTimestamp(Long processTimestamp) {
        this.processTimestamp = processTimestamp;
    }
}