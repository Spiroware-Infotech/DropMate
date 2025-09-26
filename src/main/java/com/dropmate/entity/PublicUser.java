package com.dropmate.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PublicUser {

	@Id
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String gender;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	private String address;
	private String phone;
	private String currentStatus;
	private Date lastUpdateddate;
	private Date createddate;
	private Date dob;
	private String bloodgroup;
	private String remarks;
	@Lob
	private byte[] profileImg;
	private String religion;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private User user;

}
