package org.jellyware.toolkit;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

public class URI {
	public static final String HTTP = "http";
	public static final String WS = "ws";
	public static final String HTTPS = "https";
	public static final String WSS = "wss";

	public static String path(List<String> paths, UnaryOperator<String> encoder) {
		var sj = new StringJoiner("/", "/", "");
		paths.forEach(p -> sj.add(encoder.apply(p)));
		return sj.toString();
	}

	public static String path(List<String> paths) {
		return path(paths, encoder(StandardCharsets.UTF_8));
	}

	public static String query(Map<String, String> query, UnaryOperator<String> encoder) {
		var sj = new StringJoiner("&");
		query.forEach((k, v) -> sj.add(encoder.apply(k).concat("=").concat(encoder.apply(v))));
		return sj.toString();
	}

	public static String query(Map<String, String> query) {
		return query(query, encoder(StandardCharsets.UTF_8));
	}

	public static UnaryOperator<String> encoder(Charset charset) {
		return s -> URLEncoder.encode(s, charset);
	}

	public static UnaryOperator<String> decoder(Charset charset) {
		return s -> URLDecoder.decode(s, charset);
	}

	public static String extractFileName(String contentDisposition) {
		return contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
	}

	@Getter
	@Setter
	@Embeddable
	public static class Protocol {
		private String host;
		private int port;
		private boolean secure;
	}
}
