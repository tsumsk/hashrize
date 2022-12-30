package jp.co.bzc.hashrize.vd.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jp.co.bzc.hashrize.security.AuthenticatedUser;
import jp.co.bzc.hashrize.vd.components.appnav.AppNav;
import jp.co.bzc.hashrize.vd.components.appnav.AppNavItem;
import jp.co.bzc.hashrize.vd.views.media.MediaView;
import jp.co.bzc.hashrize.vd.views.media.ModerateView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
	private static final long serialVersionUID = 1L;
	private H2 viewTitle;
	private AuthenticatedUser authenticatedUser;

	public MainLayout(AuthenticatedUser authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.getElement().setAttribute("aria-label", "Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
		H1 appName = new H1("Hashrize");
		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		Header header = new Header(appName);
		Scroller scroller = new Scroller(createNavigation());
		addToDrawer(header, scroller, createFooter());
	}

	private AppNav createNavigation() {
		AppNav nav = new AppNav();
		nav.addItem(new AppNavItem("Media", MediaView.class, "la la-image"));
		nav.addItem(new AppNavItem("Moderate", ModerateView.class, "la la-user-friends"));
		return nav;
	}

	private Footer createFooter() {
		final Footer footer = new Footer();
		authenticatedUser.get().ifPresentOrElse(user -> {
			Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
			avatar.addClassNames(Margin.End.XSMALL);

			ContextMenu userMenu = new ContextMenu(avatar);
			userMenu.setOpenOnClick(true);
			userMenu.addItem("Logout", e -> {
				authenticatedUser.logout();
			});

			Span name = new Span(user.getName());
			name.addClassNames(FontWeight.MEDIUM, FontSize.SMALL, TextColor.SECONDARY);

			footer.add(avatar, name);
			
		}, () ->{
			Anchor loginLink = new Anchor("login", "Sign in");
			footer.add(loginLink);
		});
		
		return footer;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}
}
