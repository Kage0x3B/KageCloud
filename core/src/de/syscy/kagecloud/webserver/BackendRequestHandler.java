package de.syscy.kagecloud.webserver;

import java.io.InputStream;
import java.util.List;

import com.google.common.base.Splitter;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;

public abstract class BackendRequestHandler implements IRequestHandler {
	protected String getParameter(IHTTPSession session, String parameter) {
		List<String> values = session.getParameters().get(parameter);

		if(values.size() > 0) {
			return values.get(0);
		} else {
			return "";
		}
	}

	protected String[] getArgs(IHTTPSession session) {
		String argsString = session.getUri();
		argsString = argsString.substring("/backend/".length(), session.getUri().length());
		int slashIndex = argsString.indexOf('/');
		argsString = argsString.substring(slashIndex == -1 ? 0 : slashIndex, argsString.length());

		List<String> argsList = Splitter.on('/').omitEmptyStrings().splitToList(argsString);

		return argsList.toArray(new String[argsList.size()]);
	}

	/**
	 * Create a response with unknown length (using HTTP 1.1 chunking).
	 */
	public static Response newChunkedResponse(IStatus status, String mimeType, InputStream data) {
		return NanoHTTPD.newChunkedResponse(status, mimeType, data);
	}

	/**
	 * Create a response with known length.
	 */
	public static Response newFixedLengthResponse(IStatus status, String mimeType, InputStream data, long totalBytes) {
		return NanoHTTPD.newFixedLengthResponse(status, mimeType, data, totalBytes);
	}

	/**
	 * Create a text response with known length.
	 */
	public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
		return NanoHTTPD.newFixedLengthResponse(status, mimeType, txt);
	}

	/**
	 * Create a text response with known length.
	 */
	public static Response newFixedLengthResponse(String msg) {
		return NanoHTTPD.newFixedLengthResponse(msg);
	}
}