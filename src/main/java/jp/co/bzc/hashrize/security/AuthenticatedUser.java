package jp.co.bzc.hashrize.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.AuthenticationContext;

import jp.co.bzc.hashrize.backend.model.User;
import jp.co.bzc.hashrize.backend.model.repository.RepositoryService;

@Component
public class AuthenticatedUser {

	private final RepositoryService repositoryService;
	private final AuthenticationContext authenticationContext;

	public AuthenticatedUser(AuthenticationContext authenticationContext, RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
		this.authenticationContext = authenticationContext;
	}

	public Optional<User> get() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class).map(userDetails -> repositoryService
				.getUserRepositoryService().findByUsername(userDetails.getUsername()).orElseThrow());
	}

	public void logout() {
		authenticationContext.logout();
	}

}
