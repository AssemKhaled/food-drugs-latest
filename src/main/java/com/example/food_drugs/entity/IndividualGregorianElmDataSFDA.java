package com.example.food_drugs.entity;

public class IndividualGregorianElmDataSFDA {

	String identityNumber;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String dateOfBirthGregorian;
	String activity;
	String sfdaCompanyActivity;
	
	
	
	public IndividualGregorianElmDataSFDA() {
		super();
	}
	public IndividualGregorianElmDataSFDA(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthGregorian, String activity, String sfdaCompanyActivity) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthGregorian = dateOfBirthGregorian;
		this.activity = activity;
		this.sfdaCompanyActivity = sfdaCompanyActivity;
	}
	
	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
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
	public String getDateOfBirthGregorian() {
		return dateOfBirthGregorian;
	}
	public void setDateOfBirthGregorian(String dateOfBirthGregorian) {
		this.dateOfBirthGregorian = dateOfBirthGregorian;
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
