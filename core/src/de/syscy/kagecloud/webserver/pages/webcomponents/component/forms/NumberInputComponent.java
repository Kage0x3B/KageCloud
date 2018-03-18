package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class NumberInputComponent extends BSComponent {
	private @Getter String name;
	private @Getter double min;
	private @Getter double value;
	private @Getter double step;
	private @Getter double max;

	public NumberInputComponent(String name, double value) {
		this(name, Double.MIN_VALUE, value, 1.0, Double.MAX_VALUE);
	}

	@Override
	public String toString() {
		StringBuilder attributes = new StringBuilder();
		attributes.append(" class=\"form-control\"");
		attributes.append(" type=\"number\"");
		attributes.append(" name=\"" + name + "\"");

		if(min != Double.MIN_VALUE) {
			attributes.append(" min=\"" + min + "\"");
		}

		attributes.append(" value=\"" + value + "\"");

		if(step != 1.0) {
			attributes.append(" step=\"" + step + "\"");
		}

		if(max != Double.MAX_VALUE) {
			attributes.append(" max=\"" + max + "\"");
		}

		return "<input " + attributes.toString() + ">";
	}

	public NumberInputComponent setName(String name) {
		this.name = name;

		return this;
	}

	public NumberInputComponent setMin(double min) {
		this.min = min;

		return this;
	}

	public NumberInputComponent setValue(double value) {
		this.value = value;

		return this;
	}

	public NumberInputComponent setStep(double step) {
		this.step = step;

		return this;
	}

	public NumberInputComponent setMax(double max) {
		this.max = max;

		return this;
	}
}