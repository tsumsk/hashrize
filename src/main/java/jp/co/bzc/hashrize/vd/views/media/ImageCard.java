package jp.co.bzc.hashrize.vd.views.media;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.annotation.Secured;

import com.vaadin.collaborationengine.ListKey;
import com.vaadin.collaborationengine.TopicConnection;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.avatar.AvatarGroupVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.IconSize;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextAlignment;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import jp.co.bzc.hashrize.ApplicationContextProvider;
import jp.co.bzc.hashrize.backend.model.User;
import jp.co.bzc.hashrize.backend.model.repository.RepositoryService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@CssImport(value = "./themes/hashrize/views/image-card.css")
public class ImageCard extends ListItem {
	private static final long serialVersionUID = 1L;
	@Data final static class ImageStatus {
		Boolean imageBookmarked = false;
		Boolean imageSelected = false;
		public void switchBookmarkStatus() {
			imageBookmarked = ! imageBookmarked;
		}
		public void switchSelectedStatus() {
			imageSelected = ! imageSelected;
		}
	}
	@Getter final private ImageStatus imageStatus = new ImageStatus();
	@Getter @Setter private ListKey collaborationListKey;
	@Getter final private String mediaId;
	@Getter final private Date timestamp;
	@Getter final private TopicConnection connection;
	@Getter final private String optionId;
	@Getter final private String username;

	Div div = new Div();
	Div div2 = new Div();
	@Getter Div div3 = new Div();
	final ButtonLayout buttonLayout;
	// window size for dialog
	@Data final class WindowSize {
		int size = 500;
	}
	final WindowSize windowSize = new WindowSize();

	public ImageCard(String mediaId, String text, String url, Date timestamp, String optionId, String username) {
		this(mediaId, text, url, timestamp, null, optionId, username);
	}
	public ImageCard(String mediaId, String text, String url, Date timestamp, TopicConnection connection, String optionId, String username) {
		this.mediaId = mediaId;
		this.timestamp = timestamp;
		this.connection = connection;
		this.optionId = optionId;
		this.username = username;
		if(Objects.nonNull(connection)) {
			this.buttonLayout = new ButtonLayout(this);
		} else {
			this.buttonLayout = null;
		}
		addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
				BorderRadius.LARGE);

		div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
				Margin.Bottom.XSMALL, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
		div.setHeight("240px");
//		div.addClickShortcut(Key.ENTER);
		div.addClickListener(e -> {
			// Bookmark shortcut SPACE
			ButtonLayoutForDialog buttonLayoutDialog;// = new ButtonLayoutForDialog(this);
			if(Objects.nonNull(this.buttonLayout)) {
				buttonLayoutDialog = new ButtonLayoutForDialog(this);
			} else {
				buttonLayoutDialog = null;
			}
			// inner Div
			Div dialogInnerDiv = new Div();
			dialogInnerDiv.addClassNames(Padding.NONE);
			dialogInnerDiv.setWidth("100%");
			dialogInnerDiv.setHeight("100%");
			dialogInnerDiv.addClassNames(TextAlignment.CENTER);
			// Image
			Image image = new Image(url,"");
			image.setMaxWidth("100%");
			image.setMaxHeight("100%");
			dialogInnerDiv.add(image);
			// Dialog
			Dialog dialog = new Dialog();
			// Dialog Header
			Button closeButton = new Button(new Icon("lumo", "cross"), (close) -> {
				dialog.close();
			});
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			HorizontalLayout headerLayout = new HorizontalLayout(closeButton);//, buttonLayoutDialog);
			if(Objects.nonNull(buttonLayoutDialog)) {
				headerLayout.add(buttonLayoutDialog);
			}
			headerLayout.setWidthFull();
			headerLayout.addClassNames(Display.FLEX, JustifyContent.END);
			dialog.getHeader().add(headerLayout);
			dialog.add(dialogInnerDiv);
			// Dialog size
			dialog.setWidth(windowSize.getSize(),Unit.PIXELS);
			dialog.setHeight(windowSize.getSize(),Unit.PIXELS);
			dialog.addClassNames(TextAlignment.CENTER, Padding.NONE);
			// close action listener
			dialog.addDialogCloseActionListener(ev -> {
				dialog.close();
			});
			dialog.setCloseOnEsc(true);
			dialog.setCloseOnOutsideClick(true);
			dialog.open();
		});

		Image image = new Image();
		image.setWidth("100%");
		image.setSrc(url);
		image.setAlt(text);
		div.add(image);

		// Select and Boookmark button
		div2.addClassNames(Margin.Bottom.SMALL, TextAlignment.RIGHT, Width.FULL, Background.TRANSPARENT);
		if(Objects.nonNull(buttonLayout)) {
			div2.add(buttonLayout);
		}
		add(div, div2, div3);
		addAttachListener(ev -> {
			getUI().ifPresent(ui -> ui.getPage().retrieveExtendedClientDetails(r -> windowSize.setSize(Math.min(r.getWindowInnerWidth(),r.getWindowInnerHeight()))));
		});
	}
	
	
	@Secured("ROLE_MODERATOR")
	public void setSelectedStatusFalseForModerator() {
		imageStatus.setImageSelected(false);
		div3.removeAll();
	}
	@Secured("ROLE_MODERATOR")
	@SuppressWarnings("serial")
	public void setSelectedImagesForModerator(List<SelectedImage> selectedImages) {
		imageStatus.setImageSelected(selectedImages.stream().anyMatch(si -> si.getInstagramMediaId().equals(this.mediaId)));
		RepositoryService repositoryService = (RepositoryService) ApplicationContextProvider.getBean("repositoryService", RepositoryService.class);
		div3.removeAll();
		div3.add(new AvatarGroup(selectedImages.stream().map(selectedImage -> {
					User user = repositoryService.getUserRepositoryService().findByUsername(selectedImage.getUsername()).orElseThrow();
					return new AvatarGroupItem(user.getName(), user.getProfilePictureUrl());
				}).toArray(AvatarGroupItem[]::new)) {{addThemeVariants(AvatarGroupVariant.LUMO_SMALL);}}  );
	}

	public static class ButtonLayout extends HorizontalLayout {
		private static final long serialVersionUID = 1L;
		final StarButton buttonSelect;// = new StarButton(false);
		final BookmarkButton buttonBookmark;// = new BookmarkButton(false);
		final ImageCard parent;
		public ButtonLayout(ImageCard parent) {
			this.parent = parent;
			this.buttonBookmark = new BookmarkButton(parent.imageStatus.imageBookmarked);
			this.buttonSelect = new StarButton(parent.imageStatus.imageSelected);
			add(buttonSelect, buttonBookmark);
			setSpacing(false);
			setPadding(false);
			setMargin(false);
			setWidthFull();
			addClassNames(Display.FLEX, JustifyContent.END);
			this.buttonSelect.addClassNames(Background.TRANSPARENT, Padding.NONE, Border.NONE);
			this.buttonSelect.addClickListener(e -> {
				parent.imageStatus.switchSelectedStatus();
				if(parent.imageStatus.getImageSelected()) {
					parent.div.addClassName("selected");
					parent.setCollaborationListKey(parent.connection.getNamedList(parent.optionId).insertLast(new SelectedImage(parent.username, parent.mediaId, "")).getKey());
				} else {
					parent.div.removeClassName("selected");
					parent.connection.getNamedList(parent.optionId).remove(parent.collaborationListKey);
				}
				remove(this.buttonSelect);
				addComponentAsFirst(this.buttonSelect.setSelected(parent.imageStatus.getImageSelected()));
				selectClickedAppendedAction();
			});
			buttonBookmark.addClassNames(Background.TRANSPARENT, Padding.NONE, Border.NONE);
			buttonBookmark.addClickListener(e -> {
				parent.imageStatus.switchBookmarkStatus();
				remove(buttonBookmark);
				add(buttonBookmark.setSelected(parent.imageStatus.getImageBookmarked()));
				bookmarkClickedAppendedAction();
			});
		}
		public void bookmarkClickedAppendedAction() {
			// Nothing to do
		}
		public void selectClickedAppendedAction() {
			// Nothing to do
		}
	}
	public static class ButtonLayoutForDialog extends ButtonLayout {
		private static final long serialVersionUID = 1L;
		public ButtonLayoutForDialog(ImageCard parent) {
			super(parent);
			setShortCutKey();
		}
		public void setShortCutKey() {
			// Bookmark shortcut SPACE
			super.buttonBookmark.addClickShortcut(Key.SPACE);
			super.buttonSelect.addClickShortcut(Key.ENTER);
		}
		public void selectClickedAppendedAction() {
			parent.buttonLayout.remove(parent.buttonLayout.buttonSelect);
			parent.buttonLayout.addComponentAsFirst(parent.buttonLayout.buttonSelect.setSelected(parent.imageStatus.getImageSelected()));
		}
		public void bookmarkClickedAppendedAction() {
			parent.buttonLayout.remove(parent.buttonLayout.buttonBookmark);
			parent.buttonLayout.add(parent.buttonLayout.buttonBookmark.setSelected(parent.imageStatus.getImageBookmarked()));
		}
	}
	public static class BookmarkButton extends Button {
		private static final long serialVersionUID = 1L;
		Icon iconSelected = new Icon(VaadinIcon.BOOKMARK);
		Icon iconNotSelected = new Icon(VaadinIcon.BOOKMARK_O);
		public BookmarkButton() {
			iconSelected.addClassNames(Padding.NONE, IconSize.SMALL);
			iconNotSelected.addClassNames(Padding.NONE, IconSize.SMALL);
		}
		public BookmarkButton(boolean selected) {
			this();
			if(selected) {
				setIcon(iconSelected);
			} else {
				setIcon(iconNotSelected);
			}
		}
		public BookmarkButton setSelected(boolean selected) {
			if(selected) {
				setIcon(iconSelected);
			} else {
				setIcon(iconNotSelected);
			}
			return this;
		}
	}
	public static class StarButton extends Button {
		private static final long serialVersionUID = 1L;
		Icon iconSelected = new Icon(VaadinIcon.STAR);
		Icon iconNotSelected = new Icon(VaadinIcon.STAR_O);
		public StarButton() {
			iconSelected.addClassNames(Padding.NONE, IconSize.SMALL);
			iconNotSelected.addClassNames(Padding.NONE, IconSize.SMALL);
		}
		public StarButton(boolean selected) {
			this();
			if(selected) {
				setIcon(iconSelected);
			} else {
				setIcon(iconNotSelected);
			}
		}
		public StarButton setSelected(boolean selected) {
			if(selected) {
				setIcon(iconSelected);
			} else {
				setIcon(iconNotSelected);
			}
			return this;
		}
	}
}
