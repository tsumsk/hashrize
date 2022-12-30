package jp.co.bzc.hashrize.vd.views.media;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SelectedImage implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String username; // user account
	private final String instagramMediaId;
	private final String description;

	@JsonCreator
	public SelectedImage(@JsonProperty("username") String username,
			@JsonProperty("instagramMediaId") String instagramMediaId,
			@JsonProperty("description") String description) {
		this.username = username;
		this.instagramMediaId = instagramMediaId;
		this.description = description;
	}

}
