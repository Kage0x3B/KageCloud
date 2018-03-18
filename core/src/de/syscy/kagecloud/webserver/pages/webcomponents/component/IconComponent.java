package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class IconComponent extends BSComponent {
	private @Getter @Setter SCIcon icon = SCIcon.Default.NONE;
	private @Getter @Setter boolean fullWidthIcons = false;

	public IconComponent(SCIcon icon) {
		this(icon, false);
	}

	@Override
	public String toString() {
		return "<i class=\"fa " + icon + (fullWidthIcons ? " fa-fw" : "") + "\">" + componentsToString() + "</i>";
	}
}