package jp.co.bzc.hashrize.vd.views.media;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationEngine;
import com.vaadin.collaborationengine.CollaborationList;
import com.vaadin.collaborationengine.TopicConnection;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
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

@PageTitle("[Hash Tag Contest] Judge's page")
@Route(value = "media", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./themes/hashrize/views/media-view.css")
@RolesAllowed("USER")
public class MediaView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {
	private static final long serialVersionUID = 1L;
	final private RepositoryService repositoryService;
	final private AuthenticatedUser authenticatedUser;

	public MediaView(RepositoryService repositoryService, AuthenticatedUser authenticatedUser) {
		this.repositoryService = repositoryService;
		this.authenticatedUser = authenticatedUser;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		User user = authenticatedUser.get().orElseThrow();
		String username = user.getUsername();
		String nameOfUser = user.getName();
		String pictureUrlOfUser = user.getProfilePictureUrl();
		String avatarTopicId = this.getClass().getSimpleName();
		String managementTopicId = user.getUsername();
		add(new MediaViewComposit(username, nameOfUser, pictureUrlOfUser, avatarTopicId, managementTopicId, "optionId", repositoryService));
	}

	public static class MediaViewComposit extends Composite<VerticalLayout> {
		private static final long serialVersionUID = 1L;
		final private String username;
		final private String avatarTopicId;
		final private String managementTopicId;
		final private String optionId;
		final private UserInfo localUser;
		final private RepositoryService repositoryService;
		private List<ImageCard> imageListViewCards;
		private TopicConnection connection;
		private CollaborationList collaborationList;
		private CollaborationAvatarGroup avatarGroup;
		private Paragraph selectionStatus = new Paragraph();


		private OrderedList imageContainer;
		private OrderedList layout = new OrderedList();
		public MediaViewComposit(String username, String nameOfUser, String pictureUrlOfUser, String avatarTopicId, String managementTopicId, String optionId, RepositoryService repositoryService) {
			this.username = username;
			this.avatarTopicId = avatarTopicId;
			this.managementTopicId = managementTopicId;
			this.optionId = optionId;
			this.localUser = new UserInfo(username, nameOfUser, pictureUrlOfUser);
			this.repositoryService = repositoryService;

			CollaborationEngine.getInstance().openTopicConnection(this, this.managementTopicId, localUser, this::onTopicConnection);

			this.avatarGroup = new CollaborationAvatarGroup(this.localUser, this.avatarTopicId);
			this.avatarGroup.addClassNames(Margin.Right.LARGE);
			constructUI();
		}
		private Registration onTopicConnection(TopicConnection connection) {
			this.connection = connection;
			// initalize imageListViewCards
			this.imageListViewCards = repositoryService.getMediaRepositoryService().findAll().stream()
					.filter(media -> media.getMediaType().equals("IMAGE"))
					.sorted((m1, m2) -> - m1.getTimestamp().compareTo(m2.getTimestamp()))
					.map(media -> new ImageCard(media.getId(), media.getCaption(), media.getMediaUrl(), media.getTimestamp(), this.connection, optionId, username))
					.collect(Collectors.toList());
			dataInitialize();

			this.collaborationList = connection.getNamedList(optionId);

			this.collaborationList.subscribe(change -> {
				selectionStatus.setText("You selected " + collaborationList.getItems(SelectedImage.class).size() + " photos.");
			});
			return this::onDeactivate;
		}
		private void onDeactivate() {
			this.connection = null;
			this.collaborationList = null;
		}

		private void constructUI() {
			getContent().addClassNames("image-list-view");
			getContent().addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

			HorizontalLayout container = new HorizontalLayout();
			container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

			VerticalLayout headerContainer = new VerticalLayout();
			headerContainer.addClassNames(BoxSizing.CONTENT);
			H2 header = new H2("Judge and prize Instagram photos!");
			header.addClassNames(Margin.Bottom.NONE, Margin.Top.LARGE, FontSize.XXXLARGE);
			Paragraph description = new Paragraph("You are a judge of Instagram Hashtag Contest.\n"
					+ "Please select 3 photos for prize with STAR-BUTTON. You can bookmark for convenience.\n"
					+ "Click photo for enlargement. ENTER for select, and SPACE for bookmark is available in dialog."
					);
			description.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.NONE, TextColor.SECONDARY, Whitespace.PRE_LINE);
			Paragraph avatarTitle = new Paragraph("Judges participated in");
			avatarTitle.addClassNames(Whitespace.NOWRAP);
			selectionStatus.addClassNames(Whitespace.NOWRAP);
			HorizontalLayout avatarContainer = new HorizontalLayout(avatarTitle, avatarGroup, selectionStatus);
			avatarContainer.addClassNames(Margin.Bottom.LARGE, Padding.Left.LARGE, Padding.Right.LARGE, Width.FULL, JustifyContent.BETWEEN, BoxSizing.BORDER);
			headerContainer.add(header, description);

			Select<SortFilterEnum> sortBy = new Select<>();
			sortBy.setLabel("Sort by");
			sortBy.setItems(Stream.of(SortFilterEnum.values()).collect(Collectors.toList()));
			sortBy.setItemLabelGenerator(sort -> sort.description);
			sortBy.setValue(SortFilterEnum.NewestFirst);
			sortBy.addValueChangeListener(e -> dataInitialize(e.getValue()));

			imageContainer = new OrderedList();
			imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

			layout.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
		
			container.add(headerContainer, sortBy);
			getContent().add(container, avatarContainer, imageContainer);
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
						} else if(sortFilterEnum.equals(SortFilterEnum.BookmarkedOnly)) {
							return card.getImageStatus().getImageBookmarked();
						} else if(sortFilterEnum.equals(SortFilterEnum.SelectedAndBookmarked)) {
							return card.getImageStatus().getImageSelected() || card.getImageStatus().getImageBookmarked();
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
			,BookmarkedOnly("Bookmarked only")
			,SelectedAndBookmarked("Selected And Bookmarked")
			;
			String description;
			SortFilterEnum(String description){
				this.description = description;
			}
		}
	}	
}
