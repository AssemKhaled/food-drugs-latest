package com.example.food_drugs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_warehouses")
public class Warehouse {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "activity")
	private String activity;

	@Column(name = "city")
	private String city;

	@Column(name = "address")
	private String address;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longitude")
	private Double longitude;
	
	@Column(name = "licenseNumber")
	private String licenseNumber;
	
	@Column(name = "licenseIssueDate")
	private String licenseIssueDate;
	
	@Column(name = "licenseExpiryDate")
	private String licenseExpiryDate;
	
	@Column(name = "licenseIssueDateType",columnDefinition = "int default 0")
	private Integer licenseIssueDateType;
	
	@Column(name = "licenseExpiryDateType",columnDefinition = "int default 0")
	private Integer licenseExpiryDateType;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "landAreaInSquareMeter")
	private String landAreaInSquareMeter;
	
	@Column(name = "landCoordinates")
	private String landCoordinates;
	
	@Column(name = "managerMobile")
	private String managerMobile;
	
	@Column(name = "referenceKey")
	private String referenceKey;
	
	@Column(name = "reject_reason")
	private String reject_reason;
	
	@Column(name = "delete_date")
	private String deleteDate;
	
	@Column(name = "create_date")
	private String create_date;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "userId")
	private Long userId;

	@Column(name = "regestration_to_elm_date")
	private Date regestration_to_elm_date;
	
	@Column(name = "delete_from_elm_date")
	private Date delete_from_elm_date;
	
	@Column(name = "update_date_in_elm")
	private Date update_date_in_elm;
	
	
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

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getLicenseIssueDate() {
		return licenseIssueDate;
	}

	public void setLicenseIssueDate(String licenseIssueDate) {
		this.licenseIssueDate = licenseIssueDate;
	}

	public String getLicenseExpiryDate() {
		return licenseExpiryDate;
	}

	public void setLicenseExpiryDate(String licenseExpiryDate) {
		this.licenseExpiryDate = licenseExpiryDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLandAreaInSquareMeter() {
		return landAreaInSquareMeter;
	}

	public void setLandAreaInSquareMeter(String landAreaInSquareMeter) {
		this.landAreaInSquareMeter = landAreaInSquareMeter;
	}

	public String getLandCoordinates() {
		return landCoordinates;
	}

	public void setLandCoordinates(String landCoordinates) {
		this.landCoordinates = landCoordinates;
	}

	public String getManagerMobile() {
		return managerMobile;
	}

	public void setManagerMobile(String managerMobile) {
		this.managerMobile = managerMobile;
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Integer getLicenseIssueDateType() {
		return licenseIssueDateType;
	}

	public void setLicenseIssueDateType(Integer licenseIssueDateType) {
		this.licenseIssueDateType = licenseIssueDateType;
	}

	public Integer getLicenseExpiryDateType() {
		return licenseExpiryDateType;
	}

	public void setLicenseExpiryDateType(Integer licenseExpiryDateType) {
		this.licenseExpiryDateType = licenseExpiryDateType;
	}

	

}
