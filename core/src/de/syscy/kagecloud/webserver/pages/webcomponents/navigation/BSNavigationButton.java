package de.syscy.kagecloud.webserver.pages.webcomponents.navigation;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class BSNavigationButton extends BSNavigationComponent {
	private @Getter @Setter String title;
	private @Getter @Setter String linkDestination;
	private @Getter @Setter SCIcon icon;

	@Override
	public String toString() {
		return "<a href=\"" + linkDestination + "\"><i class=\"fa " + icon + " fa-fw\"></i> " + title + "</a>";
	}
}