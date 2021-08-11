package com.partinizer.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "partygoers")
public class Partygoer extends DateAudit {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonIgnore
	@NotBlank
	private String password;

	@NotBlank
	private String email;

	@NotBlank
	@Indexed
	private String username;

	@NotBlank
	private String firstname;

	@NotBlank
	private String lastname;

	private Set<Role> roles = new HashSet<>();

	private List<String> friends = new ArrayList<>();

	private List<FriendRequest> friendReqSent = new ArrayList<>();

	private List<FriendRequest> friendReqReceived = new ArrayList<>();

	@DBRef(lazy = true)
	protected List<Party> myParties = new ArrayList<>();

	@DBRef(lazy = true)
	protected List<Party> myParticipations = new ArrayList<>();

	@DBRef(lazy = true)
	protected List<Party> invitations = new ArrayList<>();

	public Partygoer() {
	}

	public Partygoer(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
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

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}

	public List<FriendRequest> getFriendReqSent() {
		return friendReqSent;
	}

	public void setFriendReqSent(List<FriendRequest> friendReqSent) {
		this.friendReqSent = friendReqSent;
	}

	public List<FriendRequest> getFriendReqReceived() {
		return friendReqReceived;
	}

	public void setFriendReqReceived(List<FriendRequest> friendReqReceived) {
		this.friendReqReceived = friendReqReceived;
	}

	public List<Party> getMyParties() {
		return myParties;
	}

	public void setMyParties(List<Party> myParties) {
		this.myParties = myParties;
	}

	public List<Party> getMyParticipations() {
		return myParticipations;
	}

	public void setMyParticipations(List<Party> myParticipations) {
		this.myParticipations = myParticipations;
	}

	public List<Party> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<Party> invitations) {
		this.invitations = invitations;
	}

}
