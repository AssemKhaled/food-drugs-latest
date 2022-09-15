package com.example.food_drugs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of tc_user_client_inventory used in type 4 to save his inventory
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_inventory")
public class userClientInventory {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "inventoryid")
	private Long inventoryid;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getInventoryid() {
		return inventoryid;
	}

	public void setInventoryid(Long inventoryid) {
		this.inventoryid = inventoryid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}

