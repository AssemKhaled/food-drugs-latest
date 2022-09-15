package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface IUserService {

	 User getName();
	 User findById(Long userId);
	 ResponseEntity<?> findUserById(String TOKEN,Long userId,Long loggedUserId);
	 ResponseEntity<?> usersOfUser(String TOKEN,Long userId,Long loggedUserId,int offset,String search,int active,String exportData);
	 ResponseEntity<?> createUser(String TOKEN,User user,Long userId);
	 ResponseEntity<?> editUser(String TOKEN,User user,Long userId);
	 List<Integer> checkUserDuplication(User user);
	 ResponseEntity<?> deleteUser(String TOKEN,Long userId , Long deleteUserId);
	 ResponseEntity<?> activeUser(String TOKEN,Long userId , Long activeUserId);

	 ResponseEntity<?> getUserRole(Long userId);
	 Boolean checkIfParentOrNot(Long parentId,Long childId,Integer parentType ,Integer childTye);
	 ResponseEntity<?> saveUser(Long parentId , User user);
	 ResponseEntity<?> updateUser(Long parentId , User user);
	 List<User> getAllParentsOfuser(User user,Integer accountType);

	
	 ResponseEntity<?> getUserSelect(String TOKEN,Long userId);
	 ResponseEntity<?> getVendorSelect(String TOKEN,Long userId);
	 ResponseEntity<?> getClientSelect(String TOKEN,Long vendorId);

	 ResponseEntity<?> updateExpData();

	 List<User>getAllChildernOfUser(Long userId);
	 List<User>getActiveAndInactiveChildern(Long userId);
	 void resetChildernArray();

	 ResponseEntity<?> getUserSelectWithChild(String TOKEN,Long userId);



}
