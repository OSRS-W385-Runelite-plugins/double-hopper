package com.w385.hopper.core;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public final class CsvSerializer {

	private static final String LINE_SEPARATOR = "\n";

	private static final String VALUE_SEPARATOR = ",";

	/**
	 * Serializes the given map to CSV format, without headings
	 * To include headings, @see {@link #serialize(List)}.
	 *
	 * @param map - the map to serialize
	 *
	 * @return - the serialized map, null if none, blank string if empty map
	 */
	public String serialize(Map<String, String> map) {
		if (map == null)
			return null;

		return String.join(VALUE_SEPARATOR, map.values());
	}

	/**
	 * Serializes one or several maps with headings
	 * Headings are the first map's keys
	 *
	 * @param maps - the map(s) to serialize
	 *
	 * @return - the serialized map(s), null if none, blank string if empty list
	 */
	public String serialize(List<Map<String, String>> maps) {
		if (maps == null)
			return null;

		String headings = String.join(VALUE_SEPARATOR, maps.get(0).keySet());

		String lines =  maps.stream()
			.map(this::serialize)
			.collect(joining(LINE_SEPARATOR));

		return headings + LINE_SEPARATOR + lines;
	}
}
