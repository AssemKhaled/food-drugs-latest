package com.example.food_drugs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of tc_user_client_warehouse used in type 4 to save his warehouse
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_warehouse")
public class userClientWarehouse {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "warehouseid")
	private Long warehouseid;

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

	public Long getWarehouseid() {
		return warehouseid;
	}

	public void setWarehouseid(Long warehouseid) {
		this.warehouseid = warehouseid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
