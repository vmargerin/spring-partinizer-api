package com.partinizer.rest.dto;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.partinizer.domain.Party;

public class InvitationDto implements Dto {

	@NotBlank
	protected final String partyId;

	@NotBlank
	protected final String title;

	@NotBlank
	protected final String creator;

	@NotNull
	@FutureOrPresent
	protected final OffsetDateTime startDate;

	@NotNull
	@Future
	protected final OffsetDateTime endDate;

	public InvitationDto(final String partyId, final String title, final String creator, final OffsetDateTime startDate,
			final OffsetDateTime endDate) {
		this.partyId = partyId;
		this.title = title;
		this.creator = creator;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static InvitationDto convertToInvitationDto(final Party party) {
		var invitationDto = new InvitationDto(party.getId(), party.getTitle(), party.getCreator().getUsername(),
				party.getSchedule().getStartDate(), party.getSchedule().getEndDate());
		return invitationDto;
	}

	public static List<InvitationDto> convertToInvitationsDtos(final Collection<Party> invitations) {
		return invitations.stream().map(invitation -> convertToInvitationDto(invitation)).collect(Collectors.toList());
	}

	public String getPartyId() {
		return partyId;
	}

	public String getTitle() {
		return title;
	}

	public OffsetDateTime getStartDate() {
		return startDate;
	}

	public OffsetDateTime getEndDate() {
		return endDate;
	}

	public String getCreator() {
		return creator;
	}

}
