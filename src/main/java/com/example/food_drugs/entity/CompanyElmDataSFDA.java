package com.example.food_drugs.entity;

public class CompanyElmDataSFDA {
	
	String identityNumber;
	String commercialRecordNumber;
	String commercialRecordIssueDateHijri;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String managerName;
	String managerPhoneNumber;
	String managerMobileNumber;
	String activity;
	String sfdaCompanyActivity;
	
	
	
	
	public CompanyElmDataSFDA() {
		super();
	}
	
	public CompanyElmDataSFDA(String identityNumber, String commercialRecordNumber,
			String commercialRecordIssueDateHijri, String phoneNumber, String extensionNumber, String emailAddress,
			String managerName, String managerPhoneNumber, String managerMobileNumber, String activity,
			String sfdaCompanyActivity) {
		super();
		this.identityNumber = identityNumber;
		this.commercialRecordNumber = commercialRecordNumber;
		this.commercialRecordIssueDateHijri = commercialRecordIssueDateHijri;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.managerName = managerName;
		this.managerPhoneNumber = managerPhoneNumber;
		this.managerMobileNumber = managerMobileNumber;
		this.activity = activity;
		this.sfdaCompanyActivity = sfdaCompanyActivity;
	}
	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getCommercialRecordNumber() {
		return commercialRecordNumber;
	}
	public void setCommercialRecordNumber(String commercialRecordNumber) {
		this.commercialRecordNumber = commercialRecordNumber;
	}
	public String getCommercialRecordIssueDateHijri() {
		return commercialRecordIssueDateHijri;
	}
	public void setCommercialRecordIssueDateHijri(String commercialRecordIssueDateHijri) {
		this.commercialRecordIssueDateHijri = commercialRecordIssueDateHijri;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getExtensionNumber() {
		return extensionNumber;
	}
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerPhoneNumber() {
		return managerPhoneNumber;
	}
	public void setManagerPhoneNumber(String managerPhoneNumber) {
		this.managerPhoneNumber = managerPhoneNumber;
	}
	public String getManagerMobileNumber() {
		return managerMobileNumber;
	}
	public void setManagerMobileNumber(String managerMobileNumber) {
		this.managerMobileNumber = managerMobileNumber;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getSfdaCompanyActivity() {
		return sfdaCompanyActivity;
	}
	public void setSfdaCompanyActivity(String sfdaCompanyActivity) {
		this.sfdaCompanyActivity = sfdaCompanyActivity;
	}
	
	

}
