package jp.co.bzc.hashrize.backend.model.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.bzc.hashrize.backend.model.User;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
class MockUserRepository {

	private List<User> users;

	@Autowired PasswordEncoder passwordEncoder;

	@PostConstruct
	private void initializeDummyUser() {
		try {
			File resource = new ClassPathResource("dummy-users.json").getFile();
			String json = new String(Files.readAllBytes(resource.toPath()));
			ObjectMapper mapper = new ObjectMapper();
			users = mapper.readValue(json, new TypeReference<List<User>>(){}).stream().map(u -> new User(u.getUsername(),u.getName(),passwordEncoder.encode(u.getPassword()), u.getRoles(), u.getProfilePictureUrl())).collect(Collectors.toList());
		} catch (IOException e) {
			log.error("[ERROR] Check dummy-users.json exists in resources.");
		}
	}

	public Optional<User> findByUsername(String username) {
		return users.stream().filter(u -> u.getUsername().equals(username)).findAny();
	}

	public List<User> findAll() {
		return users;
	}
}
