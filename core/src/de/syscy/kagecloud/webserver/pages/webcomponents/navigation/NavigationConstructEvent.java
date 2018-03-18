package de.syscy.kagecloud.webserver.pages.webcomponents.navigation;

import de.syscy.kagecloud.plugin.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class NavigationConstructEvent extends Event {
	private @Getter @Setter BSNavigation navigation;
}