package de.syscy.kagecloud.webserver.backendhandler;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings("unused") //The values are used by GSON via reflections.
public class DataTableResponse {
	private final int draw;
	private final long recordsTotal;
	private final long recordsFiltered;

	private final List<String[]> data;
}