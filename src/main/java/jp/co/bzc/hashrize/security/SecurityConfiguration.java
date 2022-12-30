package jp.co.bzc.hashrize.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jp.co.bzc.hashrize.vd.views.login.LoginView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

	public static final String LOGOUT_URL = "/";

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		setLoginView(http, LoginView.class, LOGOUT_URL);
	}
}
