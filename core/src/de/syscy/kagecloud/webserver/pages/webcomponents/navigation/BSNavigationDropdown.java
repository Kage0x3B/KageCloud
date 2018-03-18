package de.syscy.kagecloud.webserver.pages.webcomponents.navigation;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.NavigationDropdownLevel;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class BSNavigationDropdown extends BSNavigationComponent {
	private @Getter @Setter String title;
	private @Getter @Setter NavigationDropdownLevel dropdownLevel;
	private @Getter @Setter SCIcon icon;

	public BSNavigationDropdown(String title, SCIcon icon) {
		this(title, NavigationDropdownLevel.SECOND, icon);
	}

	@Override
	public String toString() {
		StringBuilder dropdown = new StringBuilder();
		dropdown.append("<a href=\"#\"><i class=\"fa " + icon + " fa-fw\"></i> " + title + "<span class=\"fa arrow\"></span></a>");

		dropdown.append("<ul class=\"nav " + dropdownLevel + "\">");

		for(BSNavigationComponent component : components) {
			dropdown.append("<li>");
			dropdown.append(component.toString());
			dropdown.append("</li>");
		}

		dropdown.append("</ul>");

		return dropdown.toString();
	}
}