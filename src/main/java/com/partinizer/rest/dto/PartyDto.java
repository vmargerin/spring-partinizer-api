package com.partinizer.rest.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import com.partinizer.domain.Invitation;
import com.partinizer.domain.InvitationStatus;
import com.partinizer.domain.Party;
import com.partinizer.domain.PartyStatus;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.Schedule;
import com.partinizer.domain.validation.OnCreate;
import com.partinizer.domain.validation.OnUpdate;

public class PartyDto implements Dto {

	@Null(groups = OnCreate.class)
	@NotNull(groups = OnUpdate.class)
	private final String id;

	private String creator;

	private PartyStatus status;

	@NotBlank
	@Size(min = 3, max = 100)
	private final String title;

	@Size(min = 0, max = 300)
	private String description;

	@NotNull
	@Valid
	private final Schedule schedule;

	@NotNull
	@Valid
	private Collection<InviteeDto> invitees;

	@Min(value = 0)
	@Valid
	private int participantCount;

	public PartyDto(final String id, final String title, final Schedule schedule) {
		this.id = id;
		this.title = title;
		this.schedule = schedule;
	}

	public static PartyDto convertToPartyDto(final Party party) {

		final Collection<InviteeDto> invitations = party.getInvitations().stream()
				.map(invitation -> new InviteeDto(invitation.getPartygoer().getId(),
						invitation.getPartygoer().getUsername(), invitation.getStatus()))
				.collect(Collectors.toList());

		final PartyDto dto = new PartyDto(party.getId(), party.getTitle(), party.getSchedule());
		dto.setDescription(party.getDescription());
		dto.setCreator(party.getCreator().getUsername());
		dto.setStatus(party.getStatus());
		dto.setInvitees(invitations);
		dto.setParticipantCount(party.getParticipantCount());
		return dto;
	}

	public static List<PartyDto> convertToPartyDtos(final Collection<Party> parties) {
		return parties.stream().map(party -> convertToPartyDto(party)).collect(Collectors.toList());
	}

	public static Party convertToParty(final PartyDto partyDto, final String id) {
		final List<Invitation> invitations = partyDto.getInvitees().stream()
				.map((partygoerId) -> new Invitation(new Partygoer(partygoerId.getId()), InvitationStatus.PENDING))
				.collect(Collectors.toList());
		final Party party = new Party(partyDto.getTitle(), partyDto.getSchedule(), new Partygoer(id));
		party.setDescription(partyDto.getDescription());
		party.setStatus(partyDto.getStatus());
		party.setInvitations(invitations);
		party.setParticipantCount(partyDto.getParticipantCount());
		return party;
	}

	public String getId() {
		return id;
	}

	public String getCreator() {
		return creator;
	}

	public String getTitle() {
		return title;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<InviteeDto> getInvitees() {
		return invitees;
	}

	public void setInvitees(Collection<InviteeDto> invitees) {
		this.invitees = invitees;
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public PartyStatus getStatus() {
		return status;
	}

	public void setStatus(PartyStatus status) {
		this.status = status;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

}
