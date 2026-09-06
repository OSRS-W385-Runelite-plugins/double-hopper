package com.w385.hopper.core;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * CSV serializer and deserializer
 */
public final class CsvCodec {

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

	/**
	 * Deserializes a CSV content and stores it into a key-value format
	 * Headings (columns name) must be unique and non-blank
	 * CSV must not contain line feeds and comma in headings and values
	 *
	 * @param csv - the file contents to parse
	 *
	 * @return - maps where keys are the headings of the CSV, and values the
	 * 	corresponding values
	 * 	In case of null or blank input or invalid heading, empty List is returned
	 */
	public List<Map<String, String>> deserialize(String csv) {
		if (csv == null)
			return List.of();

		String[] lines = splitLines(csv);

		String[] headings = splitValues(lines[0]);
		if (containsInvalidHeadings(headings))
			return List.of();

		var maps = new ArrayList<Map<String, String>>();

		for (int i = 1; i < lines.length; i++) {
			String[] values = splitValues(lines[i]);
			if (values.length == headings.length)
				maps.add(zipmap(headings, values));
		}

		return maps;
	}

	/**
	 * Splits a string from a regex BUT KEEPING trailing blank elements
	 *
	 * @param string - the string to split
	 * @param separator - the text to cut
	 *
	 * @return - the tokens
	 */
	private static String[] splitAndKeepEmpty(String string, String separator) {
		return string.split(separator, -1);
	}

	/**
	 * Splits the CSV content into lines, keeping trailing blank ones
	 *
	 * @param csv - the CSV content to split
	 *
	 * @return - the CSV lines
	 */
	private static String[] splitLines(String csv) {
		return splitAndKeepEmpty(csv, LINE_SEPARATOR);
	}

	/**
	 * Splits the CSV values, keeping trailing blank elements
	 *
	 * @param values - the values to split
	 *
	 * @return - the CSV values
	 */
	private static String[] splitValues(String values) {
		return splitAndKeepEmpty(values, VALUE_SEPARATOR);
	}

	/**
	 * Checks if the given array contains blank strings
	 *
	 * @param strings - the string to check
	 *
	 * @return - true if the array contains blank string, false otherwise
	 */
	private static boolean containsBlank(String[] strings) {
		for (String string : strings) {
			if (string.isBlank())
				return true;
		}

		return false;
	}

	/**
	 * Checks if the array contains duplicate strings
	 *
	 * @param strings - the strings to check
	 *
	 * @return - true if the array contains duplicates, false otherwise
	 */
	private static boolean containsDuplicates(String[] strings) {
		Set<String> uniques = Arrays.stream(strings).collect(toSet());
		return uniques.size() != strings.length;
	}

	/**
	 * Checks if the given array contains invalid headings
	 *
	 * @param headings - the headings to check
	 *
	 * @return - true if the array contains blank or duplicate headings
	 */
	private static boolean containsInvalidHeadings(String[] headings) {
		return containsBlank(headings) || containsDuplicates(headings);
	}

	/**
	 * Creates a map from its keys and values
	 *
	 * @param keys - the keys to put in the map
	 * @param values - the values to put in the map, for the key at the same index
	 *
	 * @return - the created map
	 */
	private static Map<String, String> zipmap(String[] keys, String[] values) {
		var map = new HashMap<String, String>();

		for (int i = 0; i < keys.length; i++)
			map.put(keys[i], values[i]);

		return map;
	}
}
