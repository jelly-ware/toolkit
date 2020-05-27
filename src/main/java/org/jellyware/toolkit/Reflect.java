package org.jellyware.toolkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jellyware.beef.Beef;

public interface Reflect<T> {
	public static final String GETTER_PREFIX = "get";
	public static final String SETTER_PREFIX = "set";

	public static Optional<Method> getter(Field field) {
		return null;
		// return method(m -> m.getName()
		// .equals(GETTER_PREFIX + Character.toUpperCase(field.getName().charAt(0)) +
		// field.getName().substring(1))
		// && m.getParameterCount() == 0);
	}

	public static Optional<Method> setter(Field field) {
		return null;
		// return method(m -> m.getName()
		// .equals(SETTER_PREFIX + Character.toUpperCase(field.getName().charAt(0)) +
		// field.getName().substring(1))
		// && (m.getParameterCount() == 1 ?
		// m.getParameterTypes()[0].equals(field.getType()) : false));
	}

	public static Optional<Type> genericSuperclass(Class<?> cls, Predicate<Type> predicate) {
		Type type = cls.getGenericSuperclass();
		do {
			if (predicate.test(type))
				return Optional.of(type);
			if (type instanceof ParameterizedType)
				type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
			else if (type instanceof Class)
				type = ((Class<?>) type).getGenericSuperclass();
			else
				type = null;
		} while (type != null);
		return Optional.empty();
	}

	public static Optional<Type> genericInterface(Class<?> cls, Predicate<Type> predicate) {
		for (var i : cls.getGenericInterfaces())
			if (predicate.test(i))
				return Optional.of(i);
		return Optional.empty();
	}

	public static Stream<Type> genericInterfaces(Class<?> cls, Predicate<Type> predicate) {
		var is = new HashSet<Type>();
		for (var i : cls.getGenericInterfaces())
			if (predicate.test(i))
				is.add(i);
		return is.stream();
	}

	public static <T, U> Invoker<T, U> method(Method method) {
		return (obj, args) -> {
			try {
				method.setAccessible(true);
				return (U) method.invoke(obj, args);
			} catch (Exception e) {
				if (InvocationTargetException.class.isAssignableFrom(e.getClass()))
					throw Beef.uncheck((Exception) e.getCause());
				throw Beef.uncheck(e);
			}
		};
	}

	public static <T> Constructor<T> constructor(java.lang.reflect.Constructor<T> constructor) {
		return args -> {
			try {
				constructor.setAccessible(true);
				return constructor.newInstance(args);
			} catch (Exception e) {
				if (InvocationTargetException.class.isAssignableFrom(e.getClass()))
					throw Beef.uncheck((Exception) e.getCause());
				throw Beef.uncheck(e);
			}
		};
	}

	public static interface Invoker<T, U> {
		U execute(T obj, Object... args);

		default U executeStatic(Object... args) {
			return execute(null, args);
		}
	}

	@FunctionalInterface
	public static interface Constructor<T> {
		T execute(Object... args);
	}
}
