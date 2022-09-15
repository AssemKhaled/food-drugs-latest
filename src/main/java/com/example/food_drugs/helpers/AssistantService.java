package com.example.food_drugs.helpers;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.AuditGObject;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author Assem
 */
public interface AssistantService {
    List<Long> getChildrenOfUser(Long userId);
    List<Long> getUserChildrens(Long userId);
    void saveOnAuditChanges(Long userId , AuditGObject<?> oldEntity , AuditGObject<?> newEntity , String action,Long entityId);
}
