package jp.co.bzc.hashrize.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstagramMedias {
	@JsonProperty("data")
	private List<InstagramMedia> data;
}
