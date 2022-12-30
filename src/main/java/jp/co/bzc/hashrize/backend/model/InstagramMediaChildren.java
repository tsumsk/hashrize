package jp.co.bzc.hashrize.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restfb.types.instagram.IgMediaChild;

import lombok.Data;

@Data
public class InstagramMediaChildren {
	@JsonProperty("data")
	private List<IgMediaChild> data;
}
