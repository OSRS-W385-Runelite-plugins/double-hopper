package com.w385.hopper.core;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Very basic CSV deserializer, doesn't support quotes or escaping
 */
public final class CsvDeserializer {

	private static final String LINE_SEPARATOR = "\n";

	private static final String VALUE_SEPARATOR = ",";

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

		String[] lines = splitAndKeepEmpty(csv, LINE_SEPARATOR);

		String[] headings = splitAndKeepEmpty(lines[0], VALUE_SEPARATOR);
		if (containsInvalidHeadings(headings))
			return List.of();

		var maps = new ArrayList<Map<String, String>>();

		for (int i = 1; i < lines.length; i++) {
			String[] values = splitAndKeepEmpty(lines[i], VALUE_SEPARATOR);
			if (values.length != headings.length)
				continue;

			var map = zipmap(headings, values);
			maps.add(map);
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
