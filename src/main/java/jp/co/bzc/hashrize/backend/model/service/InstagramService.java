package jp.co.bzc.hashrize.backend.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Account;
import com.restfb.types.Page;
import com.restfb.types.instagram.IgUser;

@Service
public class InstagramService {

	@Value("${app.facebook.access-token}") String accesstoken;
	@Value("${app.facebook.pagename-linked-with-instagram}") String pageName;

	public void printHashtagTopMedias(String hashtag) {
		FacebookClient facebookClient = new DefaultFacebookClient(accesstoken, Version.VERSION_15_0);
		// your own facebook page list .
		Connection<Account> connection = facebookClient.fetchConnection("/me/accounts", Account.class);
		// query by pageId
//		Account account = connection.getData().stream().filter(acc -> acc.getId().equals(pageID)).findAny().orElseThrow();
		// query by pagename
		Account account = connection.getData().stream().filter(acc -> acc.getName().equals(pageName)).findAny().orElseThrow();
//		String pageAccessToken = account.getAccessToken();
		String pageId = account.getId();

		// get Instagram user (igUser) from pageId
		Page page = facebookClient.fetchObject(pageId, Page.class, Parameter.with("fields", "instagram_business_account"));
		IgUser igUser = page.getInstagramBusinessAccount();

		// get hashtagId for hashtag
		JsonObject nodeId = facebookClient.fetchObject("ig_hashtag_search", JsonObject.class,
				Parameter.with("user_id", igUser.getId()), Parameter.with("q", hashtag.replace("#", "")));
		JsonArray nodeArray = nodeId.get("data").asArray();
		String hashtagId = null;
		if (nodeArray.size() > 0) {
			hashtagId = nodeArray.get(0).asObject().getString("id", null);
		}
		// get Top Media from hashtagId
		String fields = "timestamp,media_url,media_type,id,children{media_url,permalink},permalink,caption";
		JsonObject mediasJosonObject = facebookClient.fetchObject(hashtagId + "/top_media", JsonObject.class,
				Parameter.with("user_id", igUser.getId()),
				Parameter.with("fields", fields));
		System.out.println(mediasJosonObject);
	}
}
