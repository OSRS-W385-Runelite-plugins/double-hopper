package com.w385.hopper.core;

import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CsvSerializerTest {

	private static final String LINE_SEPARATOR = "\n";

	private final CsvSerializer serializer = new CsvSerializer();

	@Test
	public void noMapNullCsv() {
		Map<String, String> map = null;
		String csv = this.serializer.serialize(map);
		assertThat(csv, is(nullValue()));
	}

	@Test
	public void emptyMapBlankCsv() {
		var map = new HashMap<String, String>();
		String csv = this.serializer.serialize(map);
		assertThat(csv, is(""));
	}

	@Test
	public void singleMap_csvContainsOnlyValues() {
		var map = Map.of("", "values");
		String csv = this.serializer.serialize(map);
		assertThat(csv, is("values"));
	}

	@Test
	public void singleMap_valuesAreCommaSeparated() {
		var map = Map.of("key1", "", "key2", "");
		String csv = this.serializer.serialize(map);
		assertThat(csv, containsString(","));
	}

	@Test
	public void singleMap_valuesMayBeInInsertionOrder() {
		var map = new LinkedHashMap<String, String>() {{
			put("first", "a");
			put("second", "b");
			put("third", "c");
		}};
		String csv = this.serializer.serialize(map);
		assertThat(csv, is("a,b,c"));
	}

	@Test
	public void severalMaps_containsOneLinePerMapPlusHeadingsLine() {
		var maps = List.of(Map.of("key", ""), Map.of("key", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(lines(csv).length, is(1 + maps.size()));
	}

	@Test
	public void severalMaps_headingsAreOnFirstLine() {
		var maps = List.of(Map.of("map-keys", ""), Map.of("maps-keys", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(headings(csv), is("map-keys"));
	}

	@Test
	public void severalMaps_headingsAreCommaSeparated() {
		var maps = List.of(Map.of("key1", "", "key2", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(headings(csv), containsString(","));
	}

	@Test
	public void severalMaps_headingsMayBeInInsertionOrder() {
		List<Map<String, String>> maps = List.of(
			new LinkedHashMap<>() {{ put("well", ""); put("ordered", ""); }},
			new LinkedHashMap<>() {{ put("well", ""); put("ordered", ""); }}
		);
		String csv = this.serializer.serialize(maps);
		assertThat(headings(csv), is("well,ordered"));
	}

	@Test
	public void severalMaps_valuesAreCommaSeparated() {
		var maps = List.of(Map.of("key1", "", "key2", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(valuesLines(csv)[0], containsString(","));
	}

	@Test
	public void severalMaps_valuesAreOnCorrectRow() {
		var maps = List.of(Map.of("key", "first-map"), Map.of("key", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(valuesLines(csv)[0], is("first-map"));
	}

	@Test
	public void severalMaps_valuesAreInCorrectColumn() {
		//given
		var map = Map.of("clan", "W385", "rating", "awesome", "type", "f2p");

		// when
		String csv = this.serializer.serialize(List.of(map));

		// then
		String[] headingsInOrder = headings(csv).split(",");
		String expectedValuesOrder = Arrays.stream(headingsInOrder)
			.map(map::get)
			.collect(joining(","));
		assertThat(valuesLines(csv)[0], is(expectedValuesOrder));
	}

	@Test
	public void severalMaps_headingsTakenFromFirstMap() {
		var maps = List.of(
			Map.of("first-map-keys", ""),
			Map.of("second-map-keys", ""));
		String csv = this.serializer.serialize(maps);
		assertThat(headings(csv), is("first-map-keys"));
	}

	@Test
	public void finalTest() {
		// given
		List<Map<String, String>> maps = List.of(
			new LinkedHashMap<>() {{ put("key1", "map1-a"); put("key2", "map1-b"); }},
			new LinkedHashMap<>() {{ put("key1", "map2-a"); put("key2", "map2-b"); }});

		// when
		String csv = this.serializer.serialize(maps);

		// then
		String expectedOutput = "key1,key2" + LINE_SEPARATOR
			+ "map1-a,map1-b" + LINE_SEPARATOR
			+ "map2-a,map2-b";
		assertThat(csv, is(expectedOutput));
	}

	private static String[] lines(String csv) {
		return csv.split(LINE_SEPARATOR, -1); // keep empty lines
	}

	private static String headings(String csv) {
		return lines(csv)[0];
	}

	private static String[] valuesLines(String csv) {
		return Arrays.stream(lines(csv))
			.skip(1)
			.toArray(String[]::new);
	}
}
