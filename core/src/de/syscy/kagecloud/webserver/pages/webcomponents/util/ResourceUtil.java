package de.syscy.kagecloud.webserver.pages.webcomponents.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceUtil {
	/**
	 * Reads the entire file into a string using the specified charset.
	 * @throw GdxRuntimeException if the file handle represents a directory, doesn't exist, or could not be read.
	 */
	public String readWebServerResource(String fileName) {
		StringBuilder output = new StringBuilder(512);

		fileName = "/webinterface" + (fileName.startsWith("/") ? "" : "/") + fileName;

		InputStream classpathStream = BSComponent.class.getResourceAsStream(fileName);

		if(classpathStream == null) {
			System.err.println("Could not find " + fileName);

			return "";
		}

		try(InputStreamReader reader = new InputStreamReader(classpathStream)) {
			char[] buffer = new char[256];

			while(true) {
				int length = reader.read(buffer);

				if(length == -1) {
					break;
				}

				output.append(buffer, 0, length);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		return output.toString();
	}
}