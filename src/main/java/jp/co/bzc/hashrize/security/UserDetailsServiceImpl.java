package jp.co.bzc.hashrize.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.co.bzc.hashrize.backend.model.User;
import jp.co.bzc.hashrize.backend.model.repository.RepositoryService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final RepositoryService repositoryService;

	public UserDetailsServiceImpl(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repositoryService.getUserRepositoryService().findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + username));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthorities(user));
	}

	private static List<GrantedAuthority> getAuthorities(User user) {
		return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());

	}

}
