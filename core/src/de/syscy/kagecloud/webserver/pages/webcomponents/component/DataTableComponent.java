package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import lombok.Getter;

public class DataTableComponent extends BSComponent {
	private @Getter String tableId;
	private @Getter String[] columns;

	public DataTableComponent(String tableId, String[] columns, RootPageContainer rootPageContainer, String tableJs) {
		this.tableId = tableId;
		this.columns = columns;

		rootPageContainer.getJavaScript().add(tableJs);
	}

	@Override
	public String toString() {
		StringBuilder dataTable = new StringBuilder();

		dataTable.append("<table id=\"" + tableId + "\" class=\"display\" cellspacing=\"0\" width=\"100%\">");
		dataTable.append("<thead><tr>");

		for(String column : columns) {
			dataTable.append("<th>" + column + "</th>");
		}

		dataTable.append("</tr></thead>");
		dataTable.append("<tfoot><tr>");

		for(String column : columns) {
			dataTable.append("<th>" + column + "</th>");
		}

		dataTable.append("</tr></tfoot>");
		dataTable.append("</table>");

		return dataTable.toString();
	}
}