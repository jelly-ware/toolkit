package org.jellyware.toolkit;

public interface Type {
	public static Class<?> box(Class<?> type) {
		if (!type.isPrimitive())
			return type;
		else if (int.class.equals(type))
			return Integer.class;
		else if (double.class.equals(type))
			return Double.class;
		else if (char.class.equals(type))
			return Character.class;
		else if (boolean.class.equals(type))
			return Boolean.class;
		else if (long.class.equals(type))
			return Long.class;
		else if (float.class.equals(type))
			return Float.class;
		else if (short.class.equals(type))
			return Short.class;
		else if (byte.class.equals(type))
			return Byte.class;
		else if (void.class.equals(type))
			return Void.class;
		else
			throw new IllegalArgumentException(
					"Primitive type not supported: " + type.getName());
	}
}
