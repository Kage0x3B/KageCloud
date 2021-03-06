package de.syscy.kagecloud.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.base.Predicate;

public class SearchQueryFilter implements Predicate<Object> {
	private final boolean onInvalid;
	private boolean invalid = false;

	private String searchQuery = "";
	private boolean regexMode = false;
	private Pattern searchQueryPattern;

	public SearchQueryFilter(String searchQuery, boolean onInvalid) {
		this.onInvalid = onInvalid;

		if(searchQuery == null || searchQuery.trim().isEmpty()) {
			invalid = true;

			return;
		}

		if(searchQuery.startsWith("#")) {
			try {
				searchQuery = searchQuery.substring(1);
				searchQueryPattern = Pattern.compile(searchQuery);

				regexMode = true;
			} catch(PatternSyntaxException ex) {
				invalid = true;

				return;
			}
		}

		this.searchQuery = searchQuery.toLowerCase();
	}

	@Override
	public boolean apply(Object input) {
		if(invalid) {
			return onInvalid;
		}

		String name = input.toString().toLowerCase();

		return regexMode ? searchQueryPattern.matcher(name).matches() : name.contains(searchQuery);
	}
}