package com.dropmate.dto;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {

	private Integer userId;
	private String institutename;
	private String firstname;
	private String lastname;
	private String email;
	private String username;
	private String password;
	private String gender;

	private String city;
	private String state;
	private String country;
	private String zipcode;

	private String presentAddress;
	private String permanentAddress;
	private String mobileNo;
	private String alternateMobileNo;

	private String birthday_date;
	private String birthday_month;
	private String birthday_year;
	private String dob;
	private Date createddate;
	private boolean enabled;

	private String userCurrentStatus;
	private String joiningDate;
	private MultipartFile profilePic;
	private MultipartFile posterImg;

	private Long instituteId;
	private String religion;
	private String bloodgroup;
	private String registerNO;
	private String roll;
	private String remarks;
	private Long teacherID;
	private Long studentGroupID;
	private Long optionalSubjectID;

	private Long classId;

	private Long sectionId;
	private String studentClass;
	private String section;
	private String studentGroup;
	private String optionalSubject;
	private String extracurricularActivities;
}
