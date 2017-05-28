package de.syscy.kagecloud.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.syscy.kagecloud.KageCloud;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudReflectionListener extends Listener {
	private final Class<? extends Connection> connectionClass;
	private final HashMap<Class<?>, Method> classToMethod = new HashMap<>();

	@Override
	public void received(Connection connection, Object object) {
		Class<?> type = object.getClass();
		Method method = classToMethod.get(type);

		if(method == null) {
			if(classToMethod.containsKey(type)) {
				return; // Only fail on the first attempt to find the method.
			}
			try {
				method = getClass().getMethod("received", new Class[] { connectionClass, type });
			} catch(SecurityException ex) {
				KageCloud.logger.severe("Unable to access method: received(" + connectionClass.getSimpleName() + ", " + type.getName() + ")");

				return;
			} catch(NoSuchMethodException ex) {
				KageCloud.logger.fine("Unable to find listener method: " + getClass().getName() + "#received(\" + connectionClass.getSimpleName() + \", " + type.getName() + ")");

				return;
			} finally {
				classToMethod.put(type, method);
			}
		}
		try {
			method.invoke(this, connectionClass.cast(connection), object);
		} catch(Throwable ex) {
			if(ex instanceof InvocationTargetException && ex.getCause() != null) {
				ex = ex.getCause();
			}

			if(ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}

			throw new RuntimeException("Error invoking method: " + getClass().getName() + "#received(\" + connectionClass.getSimpleName() + \", " + type.getName() + ")", ex);
		}
	}
}