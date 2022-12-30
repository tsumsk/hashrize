package jp.co.bzc.hashrize.backend.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;

	private String username;	// unique account
	private String name;		// display name
	private String password;	// hashed passowrd
	private Set<Role> roles;	// roles
	private String profilePictureUrl; // url

	public List<GrantedAuthority> getAuthorities(){
		return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
	}
	public boolean isAccountNonExpired() { return true; }
	public boolean isAccountNonLocked() { return true; }
	public boolean isCredentialsNonExpired() { return true; }
	public boolean isEnabled() { return true; }
}
