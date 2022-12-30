package jp.co.bzc.hashrize.backend.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.bzc.hashrize.backend.model.InstagramMedia;
import jp.co.bzc.hashrize.backend.model.User;
import lombok.Getter;

@Service
public class RepositoryService {
	@Autowired @Getter private UserRepositoryService userRepositoryService;
	@Autowired @Getter private MediaRepositoryService mediaRepositoryService;
	
	@Service
	public static class UserRepositoryService {
		@Autowired private MockUserRepository repository;

		public Optional<User> findByUsername(String username) {
			return repository.findByUsername(username);
		}
		
		public List<User> findAll() {
			return repository.findAll();
		}
	}
	@Service
	public static class MediaRepositoryService {
		@Autowired private MockMediaRepository repository;

		public List<InstagramMedia> findAll() {
			return repository.findAll();
		}
		public Optional<InstagramMedia> findById(String id) {
			return repository.findById(id);
		}
		
	}
}
