package de.syscy.kagecloud.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventHandlerMethod {
	private final @Getter Object listener;

	private final @Getter Method method;

	public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(listener, event);
	}
}