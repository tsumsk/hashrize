package jp.co.bzc.hashrize.backend.model.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.bzc.hashrize.backend.model.InstagramMedia;
import jp.co.bzc.hashrize.backend.model.InstagramMedias;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockMediaRepository {

	private List<InstagramMedia> medias;

	@PostConstruct
	private List<InstagramMedia> initializeMedia(){
		try {
			File resource = new ClassPathResource("instagram-hashtag-mountain.json").getFile();
			String json = new String(Files.readAllBytes(resource.toPath()));
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			InstagramMedias igMedias = mapper.readValue(json, InstagramMedias.class);
			medias = igMedias.getData();
		} catch (IOException e) {
			log.error("[ERROR] Check instagram-hashtag-nature.json exists in resources");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public List<InstagramMedia> findAll(){
		return medias;
	}
	public Optional<InstagramMedia> findById(String id){
		return medias.stream().filter(media -> media.getId().equals(id)).findAny();
	}
	
}
