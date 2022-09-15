package com.example.food_drugs.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import com.example.examplequerydslspringdatajpamaven.entity.User;

/**
 * child from User to add activity and sfdaCompanyActivity to user and SFDA for column DTYPE 
 * @author fuinco
 *
 */
@Entity
@DiscriminatorValue("null")
public class UserSFDA extends User{

}
