package com.partinizer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.partinizer.domain.Party;
import com.partinizer.domain.Partygoer;
import com.partinizer.domain.Result;
import com.partinizer.domain.Schedule;
import com.partinizer.repository.PartyRepository;
import com.partinizer.repository.PartygoerRepository;
import com.partinizer.service.impl.PartyServiceImpl;

@SpringBootTest
public class PartyServiceTest {

	@MockBean
	private PartyRepository partyRepository;

	@MockBean
	private PartygoerRepository partygoerRepository;

	@Autowired
	private PartyServiceImpl partyService;

	private final String PARTY_ID = "5f87255c59e19b2e0c4df357";
	private final String TITLE = "The party";

	@Test
	public void getPartyNotExists() {
		when(partyRepository.findById(anyString())).thenReturn(Optional.empty());

		final Result<Party> result = partyService.getParty(PARTY_ID);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The party with the id " + PARTY_ID + "doesn't exist");
	}

	@Test
	public void getPartyValid() {
		final var party = new Party(TITLE, null, null);
		when(partyRepository.findById(anyString())).thenReturn(Optional.of(party));

		final Result<Party> result = partyService.getParty(PARTY_ID);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().getTitle()).isEqualTo(TITLE);
	}

	@Test
	public void searchPartieEmptyQuery() {
		final Result<Collection<Party>> result = partyService.searchParties("");

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().isEmpty()).isTrue();
	}

	@Test
	public void searchPartieNoResult() {
		final var query = "toto";
		when(partyRepository.findByTitleIgnoreCaseStartingWith(eq(query))).thenReturn(Optional.empty());

		final Result<Collection<Party>> result = partyService.searchParties(query);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().isEmpty()).isTrue();
	}

	@Test
	public void searchPartieWithResult() {
		final var query = "toto";
		when(partyRepository.findByTitleIgnoreCaseStartingWith(eq(query)))
				.thenReturn(Optional.of(Arrays.asList(new Party(TITLE, null, null))));

		final Result<Collection<Party>> result = partyService.searchParties(query);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getTarget().iterator().next().getTitle()).isEqualTo(TITLE);
	}

	@Test
	public void createPartyStartDateAfterEnd() {
		final var party = new Party(null, new Schedule(OffsetDateTime.MAX, OffsetDateTime.now()), null);

		final Result<Void> result = partyService.createParty(party);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getMessage()).isEqualTo("The party start date can be anterior than the end");
	}

	@Test
	public void createPartyValid() {
		final var party = new Party(null, new Schedule(OffsetDateTime.now(), OffsetDateTime.now()), null);

		final Result<Void> result = partyService.createParty(party);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getMessage()).isEqualTo("The party has been created.");
	}

	@Test
	public void getUserWithParties() {
		final var currentUserId = "5f87237359e19b2e0c4df359";
		when(partygoerRepository.findByIdWithParties((anyString()))).thenReturn(Optional.of(new Partygoer()));

		final Result<Partygoer> result = partyService.getUserWithParties(currentUserId);

		assertThat(result.isSuccess()).isTrue();
	}

}
