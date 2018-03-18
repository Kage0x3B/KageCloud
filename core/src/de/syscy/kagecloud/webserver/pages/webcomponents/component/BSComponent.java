package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import java.util.LinkedList;
import java.util.List;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonGroupComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.forms.FormComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.DivComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ResourceUtil;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(exclude = { "parent" })
public class BSComponent {
	protected final @Getter List<BSComponent> components = new LinkedList<>();

	protected @Getter @Setter(value = AccessLevel.PRIVATE) BSComponent parent;

	public void add(BSComponent component) {
		components.add(component);
		component.setParent(this);
	}

	/**
	 * @return itself
	 */
	public BSComponent addRS(BSComponent component) {
		add(component);

		return this;
	}

	/**
	 * @return the added component
	 */
	public <T extends BSComponent> T addRC(T component) {
		add(component);

		return component;
	}

	/**
	 * @return itself
	 */
	public BSComponent addRS(String text) {
		return addRS(new TextComponent(text));
	}

	/**
	 * @return the added component
	 */
	public TextComponent addRC(String text) {
		return addRC(new TextComponent(text));
	}

	public void add(String text) {
		add(new TextComponent(text));
	}

	/**
	 * @return the added component
	 */
	public DivComponent addDiv(String classes) {
		return addRC(new DivComponent("class=\"" + classes + "\""));
	}

	/**
	 * @return the added component
	 */
	public DivComponent addGrid(ColumnAttribute... columnAttributes) {
		StringBuilder columnAttributesString = new StringBuilder();

		for(ColumnAttribute columnAttribute : columnAttributes) {
			columnAttributesString.append(columnAttribute + " ");
		}

		return addRC(new DivComponent("class=\"" + columnAttributesString.toString() + "\""));
	}

	/**
	 * @return the added component
	 */
	public DivComponent addRow() {
		return addRC(new DivComponent("class=\"row\""));
	}

	/**
	 * @return the added component
	 */
	public FormComponent addForm() {
		return addRC(new FormComponent());
	}

	/**
	 * @return the added component
	 */
	public FormComponent addForm(boolean inline) {
		return addRC(new FormComponent().setInline(inline));
	}

	/**
	 * @return the added component
	 */
	public ButtonGroupComponent addButtonGroup() {
		return addRC(new ButtonGroupComponent());
	}

	public void include(String fileName) {
		try {
			String text = ResourceUtil.readWebServerResource(fileName);
			add(text);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isEmpty() {
		return components.isEmpty();
	}

	@Override
	public String toString() {
		return componentsToString();
	}

	protected String componentsToString() {
		StringBuilder componentsString = new StringBuilder();

		for(BSComponent component : components) {
			componentsString.append(component.toString());
		}

		return componentsString.toString();
	}
}