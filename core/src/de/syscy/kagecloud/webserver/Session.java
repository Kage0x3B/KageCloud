package de.syscy.kagecloud.webserver;

import java.util.HashMap;
import java.util.Map;

import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonClickListener;

import lombok.Data;

@Data
public class Session {
	private UUID authTokenId;

	private final Map<String, Object> data = new HashMap<>();

	private final Map<String, ButtonClickListener> clickListeners = new HashMap<>();
}