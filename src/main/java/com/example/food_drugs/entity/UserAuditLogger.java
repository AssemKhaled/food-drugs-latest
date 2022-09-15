package com.example.food_drugs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

/**
 * @author Assem
 */
@Builder
@Document(collection = "tc_user_audit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuditLogger {
    @Id
    @JsonIgnore
    private ObjectId _id;
    private Long userId;
    private String entity;
    private Long entityId;
    private String genericId;
    private String action;
    private Date date;
    private Map<String,Object> oldValue;
    private Map<String,Object> newValue;

}
