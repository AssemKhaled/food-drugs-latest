package com.example.food_drugs.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author Assem
 */
@Document(collection = "tc_emails")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailsMongo {

    @Id
    private ObjectId _id;
    private String body;
    private String subject;
    private Date time;
    private Long userId;
    private Boolean isSent;

}
