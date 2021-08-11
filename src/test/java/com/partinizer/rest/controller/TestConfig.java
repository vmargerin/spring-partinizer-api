package com.partinizer.rest.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;

import com.partinizer.domain.Partygoer;
import com.partinizer.security.CustomUserDetailsService;
import com.partinizer.security.UserPrincipal;

@TestConfiguration
public class TestConfig {

	private final String USER_ID = "5f87237359e19b2e0c4df356";

	@Bean
	public CustomUserDetailsService customUserDetailsService() {

		return new CustomUserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) {
				return UserPrincipal.create(new Partygoer(USER_ID));
			}
		};
	}

}
