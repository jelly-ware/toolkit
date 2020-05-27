package org.jellyware.toolkit;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;

public class Json {
	public static <T> T parse(JsonValue value, Class<T> type) {
		Type t = type;
		return (T) parse(value, t);
	}

	public static Object parse(String value, Type type) {
		return parse(parse(value), type);
	}

	public static Object parse(JsonValue value, Type type) {
		var jsonb = CDI.current().select(Jsonb.class, new Any.Literal()).get();
		Object req = null;
		if (Class.class.isAssignableFrom(type.getClass())) {
			var cls = org.jellyware.toolkit.Type.box((Class<?>) type);
			if (Integer.class.equals(cls))
				req = Integer.valueOf(((JsonNumber) value).intValue());
			else if (Double.class.equals(cls))
				req = Double.valueOf(((JsonNumber) value).doubleValue());
			else if (Character.class.equals(cls))
				req = Character.valueOf(((JsonString) value).getString().charAt(0));
			else if (Boolean.class.equals(cls))
				req = Boolean.valueOf(value.toString());
			else if (Long.class.equals(cls)) {
				try {
					req = Long.valueOf(((JsonNumber) value).longValue());
				} catch (Exception e) {
					req = Long.valueOf(((JsonString) value).getString());
				}
			} else if (Float.class.equals(cls))
				req = Float.valueOf(((JsonNumber) value).toString());
			else if (Short.class.equals(cls))
				req = Short.valueOf(((JsonNumber) value).toString());
			else if (Byte.class.equals(cls))
				req = Byte.valueOf(value.toString());
			else if (String.class.equals(cls))
				req = ((JsonString) value).getString();
			else
				req = jsonb.fromJson(value.toString(), cls);
		} else {
			req = jsonb.fromJson(value.toString(), type);
		}
		return req;
	}

	public static JsonValue parse(String value) {
		if (value == null)
			return JsonValue.NULL;
		if (value.equalsIgnoreCase("null"))
			return JsonValue.NULL;
		if (value.equalsIgnoreCase("undefined"))
			return JsonValue.NULL;
		if (value.equals("true"))
			return JsonValue.TRUE;
		if (value.equals("false"))
			return JsonValue.FALSE;
		try {
			return javax.json.Json.createValue(Integer.valueOf(value));
		} catch (NumberFormatException e1) {
		}
		try {
			return javax.json.Json.createValue(Long.valueOf(value));
		} catch (NumberFormatException e1) {
		}
		try {
			return javax.json.Json.createValue(new BigInteger(value));
		} catch (Exception e) {
		}
		try {
			return javax.json.Json.createValue(Double.valueOf(value));
		} catch (NumberFormatException e1) {
		}
		try {
			return javax.json.Json.createValue(new BigDecimal(value));
		} catch (Exception e) {
		}
		try {
			return javax.json.Json.createReader(new StringReader(value)).readObject();
		} catch (Exception e) {
		}
		try {
			return javax.json.Json.createReader(new StringReader(value)).readArray();
		} catch (Exception e) {
		}
		return javax.json.Json.createValue(value);
	}

	public static <T> T parse(String value, Class<T> type) {
		Type t = type;
		return (T) parse(value, t);
	}
}
