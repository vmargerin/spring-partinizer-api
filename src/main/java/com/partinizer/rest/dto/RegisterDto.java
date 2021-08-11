package com.partinizer.rest.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.partinizer.domain.Partygoer;

public class RegisterDto {

	@NotBlank
	@Size(min = 3, max = 30)
	private String username;

	@NotBlank
	@Size(min = 2, max = 50)
	private String firstname;

	@NotBlank
	@Size(min = 2, max = 50)
	private String lastname;

	@NotBlank
	@Size(max = 40)
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 20)
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public static Partygoer convertToPartygoer(final RegisterDto registerDto) {
		final Partygoer partygoer = new Partygoer();
		partygoer.setUsername(registerDto.getUsername());
		partygoer.setPassword(registerDto.getPassword());
		partygoer.setEmail(registerDto.getEmail());
		partygoer.setLastname(registerDto.getLastname());
		partygoer.setFirstname(registerDto.getFirstname());
		return partygoer;
	}

}
