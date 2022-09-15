package com.example.food_drugs.entity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "tc_positions")
public class Position {


	public Position(ObjectId _id, Date devicetime, Long deviceid, Map<String, Object> attributes) {
		super();
		this._id = _id;
		this.devicetime = devicetime;
		this.deviceid = deviceid;
		this.attributes = attributes;
	}

	public Position() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	private ObjectId _id;
	
	private String protocol;

	private Date servertime;

	private Date devicetime;

	private Date fixtime;

	private Double latitude;

	private Double longitude;

	private Double speed;

	private Long deviceid;
	
	private Map<String, Object> attributes = new LinkedHashMap<>();

	private Double expoDeltaHRTkelvins;

}
