package com.example.food_drugs.entity;

import java.util.Date;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "tc_inventory_notifications")
public class MonogInventoryNotification {

	@Id
	private ObjectId _id;
	private String type;
	@Field("inventory_id")
	private Long inventoryId;
	@Field("create_date")
	private Date createdDate;
	private NotificationAttributes attributes;
	
	public MonogInventoryNotification() {
		super();

	}

	public MonogInventoryNotification(ObjectId _id, String type, Long inventoryId, Date createdDate,
									  NotificationAttributes attributes) {
		super();
		this._id = _id;
		this.type = type;
		this.inventoryId = inventoryId;
		this.createdDate = createdDate;
		this.attributes = attributes;
	}



	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public NotificationAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(NotificationAttributes attributes) {
		this.attributes = attributes;
	}
	
	

	
}
