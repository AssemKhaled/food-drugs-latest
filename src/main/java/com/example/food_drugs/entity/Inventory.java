package com.example.food_drugs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_inventories")
public class Inventory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "inventoryNumber")
	private String inventoryNumber;

	@Column(name = "activity")
	private String activity;
	
	@Column(name = "storingCategory")
	private String storingCategory;
	
	@Column(name = "referenceKey")
	private String referenceKey;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "protocolType")
	private String protocolType;
	
	@Column(name = "delete_date")
	private String deleteDate;

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "create_date")
	private String create_date;

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Column(name = "startDate")
	private Date startDate;
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "trackerIMEI")
	private String trackerIMEI;
	
	@Column(name = "userId")
	private Long userId;
	
	@Column(name = "warehouseId")
	private Long warehouseId;

	@Column(name = "lastDataId")
	private String lastDataId;
	
	@Column(name = "lastUpdate")
	private String lastUpdate;
	
	@Column(name = "GUID")
	private String GUID;
	
	@Column(name = "emailEasyCloud")
	private String emailEasyCloud;
	
	@Column(name = "passwordEasyCloud")
	private String passwordEasyCloud;
	
	@Column(name = "APIToken")
	private String APIToken;

	@Column(name = "regestration_to_elm_date")
	private Date regestration_to_elm_date;
	
	@Column(name = "delete_from_elm_date")
	private Date delete_from_elm_date;
	
	@Column(name = "update_date_in_elm")
	private Date update_date_in_elm;

	@Column(name = "convert_to_utc")
	private Boolean convertToUtc;


	public Boolean getConvertToUtc() {
		return convertToUtc;
	}

	public void setConvertToUtc(Boolean convertToUtc) {
		this.convertToUtc = convertToUtc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getStoringCategory() {
		return storingCategory;
	}

	public void setStoringCategory(String storingCategory) {
		this.storingCategory = storingCategory;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getReject_reason() {
		return reject_reason;
	}

	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public String getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(String delete_date) {
		this.deleteDate = delete_date;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getTrackerIMEI() {
		return trackerIMEI;
	}

	public void setTrackerIMEI(String trackerIMEI) {
		this.trackerIMEI = trackerIMEI;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getLastDataId() {
		return lastDataId;
	}

	public void setLastDataId(String lastDataId) {
		this.lastDataId = lastDataId;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getGUID() {
		return GUID;
	}

	public void setGUID(String gUID) {
		GUID = gUID;
	}

	public String getEmailEasyCloud() {
		return emailEasyCloud;
	}

	public void setEmailEasyCloud(String emailEasyCloud) {
		this.emailEasyCloud = emailEasyCloud;
	}

	public String getPasswordEasyCloud() {
		return passwordEasyCloud;
	}

	public void setPasswordEasyCloud(String passwordEasyCloud) {
		this.passwordEasyCloud = passwordEasyCloud;
	}

	public String getAPIToken() {
		return APIToken;
	}

	public void setAPIToken(String aPIToken) {
		APIToken = aPIToken;
	}

	public Date getRegestration_to_elm_date() {
		return regestration_to_elm_date;
	}

	public void setRegestration_to_elm_date(Date regestration_to_elm_date) {
		this.regestration_to_elm_date = regestration_to_elm_date;
	}

	public Date getDelete_from_elm_date() {
		return delete_from_elm_date;
	}

	public void setDelete_from_elm_date(Date delete_from_elm_date) {
		this.delete_from_elm_date = delete_from_elm_date;
	}

	public Date getUpdate_date_in_elm() {
		return update_date_in_elm;
	}

	public void setUpdate_date_in_elm(Date update_date_in_elm) {
		this.update_date_in_elm = update_date_in_elm;
	}
	
	

	
	
	
}
