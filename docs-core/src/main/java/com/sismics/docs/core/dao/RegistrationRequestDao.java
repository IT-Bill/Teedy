package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.RegistrationStatus;
import com.sismics.docs.core.dao.dto.RegistrationRequestDto;
import com.sismics.docs.core.model.jpa.RegistrationRequest;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.*;

/**
 * Registration request DAO.
 * 
 * @author Copilot
 */
public class RegistrationRequestDao {
    
    /**
     * Creates a new registration request.
     * 
     * @param registrationRequest Registration request to create
     * @return ID
     */
    public String create(RegistrationRequest registrationRequest) {
        // Create the UUID
        registrationRequest.setId(UUID.randomUUID().toString());
        
        // Set creation date
        registrationRequest.setCreateDate(new Date());
        
        // Set status
        registrationRequest.setStatus(RegistrationStatus.PENDING.name());
        
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(registrationRequest);
        
        return registrationRequest.getId();
    }
    
    /**
     * Returns an active registration request by ID.
     * 
     * @param id ID
     * @return Registration request
     */
    public RegistrationRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select r from RegistrationRequest r where r.id = :id");
            q.setParameter("id", id);
            return (RegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Returns an active registration request by username.
     * 
     * @param username Username
     * @return Registration request
     */
    public RegistrationRequest getByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select r from RegistrationRequest r where r.username = :username and r.status = :status");
            q.setParameter("username", username);
            q.setParameter("status", RegistrationStatus.PENDING.name());
            return (RegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Finds all registration requests.
     * 
     * @param sortCriteria Sort criteria
     * @return List of registration requests
     */
    @SuppressWarnings("unchecked")
    public List<RegistrationRequestDto> findAll(SortCriteria sortCriteria) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Get the requests
        StringBuilder sb = new StringBuilder("select r.REG_ID_C, r.REG_USERNAME_C, r.REG_EMAIL_C, r.REG_CREATEDATE_D, r.REG_STATUS_C, r.REG_REASON_C, r.REG_IDUSER_C, r.REG_PROCESSDATE_D")
                .append(" from T_REGISTRATION_REQUEST r");
        
        // Add order
        if (sortCriteria != null) {
            sb.append(" order by r.REG_CREATEDATE_D desc");
        }
        
        // Execute query
        Query q = em.createNativeQuery(sb.toString());
        List<Object[]> l = q.getResultList();
        
        // Transform into DTOs
        List<RegistrationRequestDto> dtoList = new ArrayList<>();
        for (Object[] o : l) {
            int i = 0;
            RegistrationRequestDto dto = new RegistrationRequestDto();
            dto.setId((String) o[i++]);
            dto.setUsername((String) o[i++]);
            dto.setEmail((String) o[i++]);
            dto.setCreateTimestamp(((Date) o[i++]).getTime());
            dto.setStatus((String) o[i++]);
            dto.setRejectionReason((String) o[i++]);
            dto.setUserId((String) o[i++]);
            Date processDate = (Date) o[i];
            dto.setProcessTimestamp(processDate != null ? processDate.getTime() : null);
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    /**
     * Approve a registration request.
     * 
     * @param id Registration request ID
     * @param userId User ID doing the approval
     */
    public void approve(String id, String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        Query q = em.createQuery("update RegistrationRequest r set r.status = :status, r.userId = :userId, r.processDate = :processDate where r.id = :id");
        q.setParameter("status", RegistrationStatus.APPROVED.name());
        q.setParameter("userId", userId);
        q.setParameter("processDate", new Date());
        q.setParameter("id", id);
        q.executeUpdate();
    }
    
    /**
     * Reject a registration request.
     * 
     * @param id Registration request ID
     * @param userId User ID doing the rejection
     * @param reason Rejection reason
     */
    public void reject(String id, String userId, String reason) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        Query q = em.createQuery("update RegistrationRequest r set r.status = :status, r.userId = :userId, r.processDate = :processDate, r.rejectionReason = :reason where r.id = :id");
        q.setParameter("status", RegistrationStatus.REJECTED.name());
        q.setParameter("userId", userId);
        q.setParameter("processDate", new Date());
        q.setParameter("reason", reason);
        q.setParameter("id", id);
        q.executeUpdate();
    }
}