package com.sismics.docs.core.model.jpa;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Registration request entity.
 * 
 * @author Copilot
 */
@Entity
@Table(name = "T_REGISTRATION_REQUEST")
public class RegistrationRequest {
    /**
     * Registration request ID.
     */
    @Id
    @Column(name = "REG_ID_C", length = 36)
    private String id;
    
    /**
     * Username.
     */
    @Column(name = "REG_USERNAME_C", nullable = false, length = 50)
    private String username;
    
    /**
     * Email address.
     */
    @Column(name = "REG_EMAIL_C", nullable = false, length = 100)
    private String email;
    
    /**
     * Hashed password.
     */
    @Column(name = "REG_PASSWORD_C", nullable = false, length = 200)
    private String password;
    
    /**
     * Creation date.
     */
    @Column(name = "REG_CREATEDATE_D", nullable = false)
    private Date createDate;
    
    /**
     * Request status (PENDING, APPROVED, REJECTED).
     */
    @Column(name = "REG_STATUS_C", nullable = false, length = 10)
    private String status;
    
    /**
     * Rejection reason.
     */
    @Column(name = "REG_REASON_C", length = 500)
    private String rejectionReason;
    
    /**
     * Processing user ID.
     */
    @Column(name = "REG_IDUSER_C", length = 36)
    private String userId;
    
    /**
     * Processing date.
     */
    @Column(name = "REG_PROCESSDATE_D")
    private Date processDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public RegistrationRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegistrationRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegistrationRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public RegistrationRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RegistrationRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public RegistrationRequest setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public RegistrationRequest setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public RegistrationRequest setProcessDate(Date processDate) {
        this.processDate = processDate;
        return this;
    }
}