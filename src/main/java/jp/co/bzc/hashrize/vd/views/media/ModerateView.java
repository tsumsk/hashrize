package jp.co.bzc.hashrize.vd.views.media;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationEngine;
import com.vaadin.collaborationengine.TopicConnection;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import jp.co.bzc.hashrize.backend.model.User;
import jp.co.bzc.hashrize.backend.model.repository.RepositoryService;
import jp.co.bzc.hashrize.security.AuthenticatedUser;
import jp.co.bzc.hashrize.vd.views.MainLayout;

@PageTitle("Moderator's View")
@Route(value = "moderate", layout = MainLayout.class)
@RolesAllowed("MODERATOR")
public class ModerateView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {
	private static final long serialVersionUID = 1L;
	final private RepositoryService repositoryService;
	final private AuthenticatedUser authenticatedUser;
	final private List<User> users;

	public ModerateView(RepositoryService repositoryService, AuthenticatedUser authenticatedUser) {
		this.repositoryService = repositoryService;
		this.authenticatedUser = authenticatedUser;
		this.users = repositoryService.getUserRepositoryService().findAll();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		User user = authenticatedUser.get().orElseThrow();
		String username = user.getUsername();
//		String nameOfUser = user.getName();
		String nameOfUser = "++MODERATOR++";
		String pictureUrlOfUser = user.getProfilePictureUrl();
		String avatarTopicId = MediaView.class.getSimpleName();
		List<String> managementTopicIds = this.users.stream().map(u -> u.getUsername()).collect(Collectors.toList());
		add(new ModerateViewComposit(username, nameOfUser, pictureUrlOfUser, avatarTopicId, managementTopicIds, "optionId", repositoryService));
	}


	public static class ModerateViewComposit extends Composite<VerticalLayout> {
		private static final long serialVersionUID = 1L;
		final private String username;
		final private String avatarTopicId;
		final private List<String> managementTopicIds;
		final private String optionId;
		final private UserInfo localUser;
		final private RepositoryService repositoryService;
		private List<ImageCard> imageListViewCards;
		private List<TopicConnectionClass> topicConnectionClasses = new ArrayList<>();
		private CollaborationAvatarGroup avatarGroup;
		private Paragraph selectionStatusParagraph = new Paragraph();
		private Select<SortFilterEnum> sortBy;

		private Map<String, List<SelectedImage>> usernameToSelectedImagesMap = new HashMap<>();
		private Map<String, List<SelectedImage>> mediaIdToSelectedImagesMap() {
			return usernameToSelectedImagesMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.groupingBy(selectedImage -> selectedImage.getInstagramMediaId()));
		}

		private OrderedList imageContainer;
	
		private OrderedList layout = new OrderedList();

		public ModerateViewComposit(String username, String nameOfUser, String pictureUrlOfUser, String avatarTopicId, List<String> managementTopicIds, String optionId, RepositoryService repositoryService) {
			this.username = username;
			this.avatarTopicId = avatarTopicId;
			this.managementTopicIds = managementTopicIds;
			this.optionId = optionId;
			this.localUser = new UserInfo(this.username, nameOfUser, pictureUrlOfUser);
			this.repositoryService = repositoryService;

			this.managementTopicIds.forEach(managementTopicId -> {
				topicConnectionClasses.add( new TopicConnectionClass(this, managementTopicId, this.optionId, this.localUser) );
			});

			this.imageListViewCards = this.repositoryService.getMediaRepositoryService().findAll().stream()
					.filter(media -> media.getMediaType().equals("IMAGE"))
					.sorted((m1, m2) -> - m1.getTimestamp().compareTo(m2.getTimestamp()))
					.map(media -> new ImageCard(media.getId(), media.getCaption(), media.getMediaUrl(), media.getTimestamp(), optionId, username))
					.collect(Collectors.toList());

			this.avatarGroup = new CollaborationAvatarGroup(localUser, this.avatarTopicId);
			this.avatarGroup.addClassNames(Margin.Right.LARGE);
			constructUI();
			dataInitialize();
		}

		private void constructUI() {
			getContent().addClassNames("image-list-view");
			getContent().addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

			HorizontalLayout container = new HorizontalLayout();
			container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

			VerticalLayout headerContainer = new VerticalLayout();
			H2 header = new H2("Moderate selecting and prizing of photos");
			header.addClassNames(Margin.Bottom.NONE, Margin.Top.LARGE, FontSize.XXXLARGE);
			Paragraph description = new Paragraph("You are a moderator of Instagram Hashtag Contest.\n"
					+ "You can monitor all judge's selecting action.\n"
					);
			description.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.NONE, TextColor.SECONDARY, Whitespace.PRE_LINE);
			Paragraph avatarTitle = new Paragraph("Judges participated in");
			avatarTitle.addClassNames(Whitespace.NOWRAP);
			selectionStatusParagraph.addClassNames(Whitespace.NOWRAP);
			HorizontalLayout avatarContainer = new HorizontalLayout(avatarTitle, avatarGroup, selectionStatusParagraph);
			avatarContainer.addClassNames(Margin.Bottom.LARGE, Margin.Left.LARGE, Margin.Right.MEDIUM, Width.FULL);
			headerContainer.add(header, description);

			sortBy = new Select<>();
			sortBy.setLabel("Sort by");
			sortBy.setItems(Stream.of(SortFilterEnum.values()).collect(Collectors.toList()));
			sortBy.setItemLabelGenerator(sort -> sort.description);
			sortBy.setValue(SortFilterEnum.NewestFirst);
			sortBy.addValueChangeListener(e -> dataInitialize(e.getValue()));
			Button reloadButton = new Button(VaadinIcon.REFRESH.create(),e -> dataInitialize(sortBy.getOptionalValue().orElse(SortFilterEnum.NewestFirst)));
			HorizontalLayout sortLayout = new HorizontalLayout(sortBy, reloadButton);
			sortLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
			sortLayout.setSpacing(false);

			imageContainer = new OrderedList();
			imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

			layout.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
		
			container.add(headerContainer, sortLayout);
			getContent().add(container, avatarContainer, imageContainer);
		}

		public void dataInitializeAuto() {
			dataInitialize(sortBy.getOptionalValue().orElse(SortFilterEnum.NewestFirst));
		}
		private void dataInitialize() {
			dataInitialize(SortFilterEnum.NewestFirst);
		}
		private void dataInitialize(SortFilterEnum sortFilterEnum) {
			imageContainer.removeAll();
			imageContainer.add(
					imageListViewCards.stream()
					.filter(card -> {
						if(sortFilterEnum.equals(SortFilterEnum.SelectedOnly)) {
							return card.getImageStatus().getImageSelected();
						} else {
							return true;
						}
					})
					.sorted((c1,c2) -> {
						if(sortFilterEnum.equals(SortFilterEnum.OldestFirst)) {
							return c1.getTimestamp().compareTo(c2.getTimestamp());
						} else {
							return - c1.getTimestamp().compareTo(c2.getTimestamp());
						}
					})
					.collect(Collectors.toList())
			);
		}
		
		public enum SortFilterEnum {
			NewestFirst("Newest first")
			,OldestFirst("Oldest first")
			,SelectedOnly("Selected only")
			;
			String description;
			SortFilterEnum(String description){
				this.description = description;
			}
		}
		public static class TopicConnectionClass {
			private ModerateViewComposit parent;
			private TopicConnection connection;
			private String topicId;
			private String optionId;
			public TopicConnectionClass(ModerateViewComposit component, String topicId, String optionId, UserInfo localUser) {
				this.parent = component;
				this.topicId = topicId;
				this.optionId = optionId;
				CollaborationEngine.getInstance().openTopicConnection(component, topicId, localUser, this::onTopicConnection);
			}
			private Registration onTopicConnection(TopicConnection connection) {
				this.connection = connection;
				this.connection.getNamedList(this.optionId).subscribe(change -> {
					this.parent.usernameToSelectedImagesMap.put(topicId, change.getSource().getItems(SelectedImage.class));
					parent.imageListViewCards.forEach(card -> card.setSelectedStatusFalseForModerator());
					this.parent.mediaIdToSelectedImagesMap().entrySet().forEach(entry -> {
						this.parent.imageListViewCards.stream().filter(card -> card.getMediaId().equals(entry.getKey())).findAny().ifPresent(card -> {
							card.setSelectedImagesForModerator(entry.getValue());
						});
					});
					// Notification with image
					String nameOfUser = this.parent.repositoryService.getUserRepositoryService().findByUsername(topicId).map(user->user.getName()).orElse("");
					HorizontalLayout notificationComponents = new HorizontalLayout();
					notificationComponents.setWidth("260px");
					notificationComponents.setHeight("80px");
					notificationComponents.setDefaultVerticalComponentAlignment(Alignment.CENTER);
					SelectedImage selectedImage;
					if(Objects.isNull(change.getValue(SelectedImage.class))) {
						selectedImage = change.getOldValue(SelectedImage.class);
						notificationComponents.add(new Div(new Span(nameOfUser + " UNSELECT photo.")));
					} else {
						selectedImage = change.getValue(SelectedImage.class);
						notificationComponents.add(new Div(new Span(nameOfUser + " SELECT photo.")));
					}
					String imageUrl = this.parent.repositoryService.getMediaRepositoryService().findById(selectedImage.getInstagramMediaId()).map(media -> media.getMediaUrl()).orElse("");
					if(StringUtils.isNotEmpty(imageUrl)) {
						Image image = new Image();
						StreamResource imageResource = new StreamResource(
								"image.jpg",
								new InputStreamFactory() {
									private static final long serialVersionUID = 1L;
									@Override
									public InputStream createInputStream() {
										URL urlObject;
										try {
											urlObject = new URL(imageUrl);
											URLConnection urlConnection = urlObject.openConnection();
											InputStream inputStream = urlConnection.getInputStream();
											return inputStream;
										} catch (MalformedURLException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
										return null;
									}
								}
							);
						if(Objects.nonNull(imageResource)) {
							image.setSrc(imageResource);
							notificationComponents.add(image);
							image.setHeight("100%");
						}
					}
					Notification notification = new Notification(notificationComponents);
					notification.setDuration(2500);
					notification.setPosition(Position.BOTTOM_START);
					notification.open();
					this.parent.dataInitializeAuto();
				});
				return this::onDeactivate;
			}
			private void onDeactivate() {
				this.connection = null;
			}
		}
	}	
}
