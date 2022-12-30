package jp.co.bzc.hashrize.backend.model.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("personal")
//@ActiveProfiles("release")
public class InstagramServiceTest {

	@Autowired InstagramService instagramService;
	String hashtag = "mountain"; 

	@Test
	void printHashtagTopMediasTest() {
		instagramService.printHashtagTopMedias(hashtag);
	}
}
