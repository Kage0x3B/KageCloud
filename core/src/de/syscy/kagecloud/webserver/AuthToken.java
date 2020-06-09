package de.syscy.kagecloud.webserver;

import lombok.Data;

@Data
public class AuthToken {
	private final String player;
	private final long creationTime;
}