package com.example.food_drugs.entity;

public class IndividualHijriElmDataSFDA {

	String identityNumber;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String dateOfBirthHijri;
	String activity;
	String sfdaCompanyActivity;
	
	
	
	public IndividualHijriElmDataSFDA() {
		super();
	}
	public IndividualHijriElmDataSFDA(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthHijri, String activity, String sfdaCompanyActivity) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthHijri = dateOfBirthHijri;
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
	public String getDateOfBirthHijri() {
		return dateOfBirthHijri;
	}
	public void setDateOfBirthHijri(String dateOfBirthHijri) {
		this.dateOfBirthHijri = dateOfBirthHijri;
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
