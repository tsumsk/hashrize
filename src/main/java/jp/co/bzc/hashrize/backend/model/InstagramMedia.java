package jp.co.bzc.hashrize.backend.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstagramMedia {
	@JsonProperty("id")
	private String id;

	/**
	 * Media type. Can be CAROUSEL_ALBUM, IMAGE, or VIDEO.
	 */
	@JsonProperty("media_type")
	private String mediaType;
	/**
	 * Media URL. Will be omitted from responses if the media contains copyrighted
	 * material, or has been flagged for a copyright violation.
	 */
	@JsonProperty("media_url")
	private String mediaUrl;
	/**
	 * Permanent URL to the media.
	 */
	@JsonProperty
	private String permalink;
	/**
	 * ISO 8601 formatted creation date in UTC (default is UTC ±00:00)
	 */
	@JsonProperty
	private Date timestamp;
	/**
	 * Username of user who created the media.
	 */
//	@JsonProperty
//	private String username;

	@JsonProperty("caption")
	private String caption;

	@JsonProperty("children")
	private InstagramMediaChildren children;
	
	@JsonCreator
	InstagramMedia(
			@JsonProperty("timestamp") Date timestamp
			,@JsonProperty("media_url") String mediaUrl
			,@JsonProperty("media_type") String mediaType
			,@JsonProperty("id") String id 
			,@JsonProperty("children") InstagramMediaChildren children 
			,@JsonProperty("permalink") String permalink
			,@JsonProperty("caption") String caption 
			){
		this.timestamp = timestamp;
		this.mediaUrl = mediaUrl;
		this.mediaType = mediaType;
		this.id = id;
		this.children = children;
		this.permalink = permalink;
		this.caption = caption;
	}

}
