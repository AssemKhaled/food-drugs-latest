package com.example.food_drugs.dto.Request;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Transactional
public class NotificationSettingRequest {

    List<String> notificationType;
    List<String> notifyBy;
    List<String> emails;

}
