package de.syscy.kagecloud.webserver.pages.webcomponents.navigation;

import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class BSNavigationComponent {
	protected final @Getter List<BSNavigationComponent> components = new LinkedList<>();

	protected @Getter @Setter(value = AccessLevel.PRIVATE) BSNavigationComponent parent;

	public void add(BSNavigationComponent component) {
		components.add(component);
		component.setParent(this);
	}

	/**
	 * @return itself
	 */
	public BSNavigationComponent addRS(BSNavigationComponent component) {
		add(component);

		return this;
	}

	/**
	 * @return the added component
	 */
	public <T extends BSNavigationComponent> T addRC(T component) {
		add(component);

		return component;
	}

	@Override
	public String toString() {
		return componentsToString();
	}

	protected String componentsToString() {
		StringBuilder componentsString = new StringBuilder();

		for(BSNavigationComponent component : components) {
			componentsString.append(component.toString());
		}

		return componentsString.toString();
	}
}