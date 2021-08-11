package com.partinizer.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.partinizer.domain.validation.OnCreate;
import com.partinizer.domain.validation.OnUpdate;

@Document(collection = "parties")
public class Party {

	@Id
	@Null(groups = OnCreate.class)
	@NotNull(groups = OnUpdate.class)
	private String id;

	private PartyStatus status;

	private String title;

	private String description;

	private Schedule schedule;

	@Field("creator")
	@DBRef
	private Partygoer creator;

	private List<Invitation> invitations = new ArrayList<>();

	private int participantCount;

	public Party() {

	}

	public Party(final String id) {
		this.id = id;
	}

	public Party(final String title, final Schedule schedule, final Partygoer creator) {
		this.title = title;
		this.schedule = schedule;
		this.creator = creator;
	}

	public void setCreator(Partygoer creator) {
		this.creator = creator;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Partygoer getCreator() {
		return creator;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public List<Invitation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}

	public PartyStatus getStatus() {
		return status;
	}

	public void setStatus(PartyStatus status) {
		this.status = status;
	}
}
