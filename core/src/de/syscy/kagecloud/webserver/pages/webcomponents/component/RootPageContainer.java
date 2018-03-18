package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import java.util.ArrayList;
import java.util.List;

import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.HComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.navigation.BSNavigation;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ResourceUtil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class RootPageContainer extends BSComponent {
	private final @Getter Session session;
	private final @Getter boolean empty;

	private @Getter @Setter String title;

	private @Getter List<String> headContents = new ArrayList<>();
	private @Getter List<String> bodyContents = new ArrayList<>();
	private @Getter List<String> javaScript = new ArrayList<>();

	@Override
	public String toString() {
		StringBuilder page = new StringBuilder();

		page.append("<!DOCTYPE html>");
		page.append("<html lang=\"en\">");

		page.append("<head>");
		buildHead(page);
		page.append("</head>");

		page.append("<body>");
		buildBody(page);
		page.append("</body>");

		page.append("</html>");

		return page.toString();
	}

	protected void buildHead(StringBuilder page) {
		page.append("<meta charset=\"utf-8\">");
		page.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
		page.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\">");
		page.append("<meta name=\"author\" content=\"Kage0x3B\">");

		page.append("<title>" + getTitle() + " - KageCloud</title>");

		page.append("<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">");
		page.append("<link href=\"css/metisMenu.min.css\" rel=\"stylesheet\">");
		page.append("<link href=\"https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css\" rel=\"stylesheet\">");
		page.append("<link href=\"css/sb-admin-2.css\" rel=\"stylesheet\">");
		page.append("<link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\" rel=\"stylesheet\" integrity=\"sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN\" crossorigin=\"anonymous\">");

		for(String s : headContents) {
			page.append(s);
		}

		page.append("<!--[if lt IE 9]>");
		page.append("<script src=\"https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js\"></script>");
		page.append("<script src=\"https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js\"></script>");
		page.append("<![endif]-->");
	}

	protected void buildBody(StringBuilder page) {
		if(empty) {
			page.append(componentsToString());
		} else {
			page.append("<div id=\"wrapper\">");
			page.append(BSNavigation.getDefaultNavigation().toString());
			page.append("<div id=\"page-wrapper\">");
			page.append(componentsToString());
			page.append("</div>");
			page.append("</div>");
		}

		page.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
		page.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>");
		page.append("<script src=\"js/metisMenu.min.js\"></script>");
		page.append("<script src=\"https://cdn.datatables.net/1.10.15/js/jquery.dataTables.min.js\"></script>");
		page.append("<script src=\"js/sb-admin-2.js\"></script>");

		for(String s : bodyContents) {
			page.append(s);
		}

		page.append("<script>");
		page.append("$('.tooltips').tooltip({selector: \"[data-toggle=tooltip]\",container: \"body\"})");
		page.append("</script>");

		page.append("<script type=\"text/javascript\">");
		page.append(ResourceUtil.readWebServerResource("js/ajaxHelper.js"));

		for(String s : javaScript) {
			page.append(s);
		}
		page.append("</script>");
	}

	public void addTitle() {
		addRow().addGrid(ColumnAttribute.LG_12).add(new HComponent(1, "class=\"page-header\"").addRS(getTitle()));
	}
}