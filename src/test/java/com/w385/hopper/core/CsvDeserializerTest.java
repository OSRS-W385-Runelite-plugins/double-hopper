package com.w385.hopper.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public final class CsvDeserializerTest {

	private final CsvCodec deserializer = new CsvCodec();

	@Test
	public void nullCsv_noResult() {
		String noCsv = null;
		List<Map<String, String>> result = this.deserializer.deserialize(noCsv);
		assertThat(result.size(), is(0));
	}

	@Test
	public void emptyCsv_noResult() {
		String emptyCsv = "";
		List<Map<String, String>> result = this.deserializer.deserialize(emptyCsv);
		assertThat(result.size(), is(0));
	}

	@Test
	public void linesAreSeparatedByLineFeed() {
		String csv = "heading\nvalue";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps, equalTo(List.of(Map.of("heading", "value"))));
	}

	@Test
	public void headingsAreOnFirstLine() {
		String csv = "heading\nvalue";
		Set<String> headings = this.deserializer.deserialize(csv).get(0).keySet();
		assertThat(headings, equalTo(Set.of("heading")));
	}

	@Test
	public void headingsAreCommaSeparated() {
		String csv = "first,second\na,b";
		Set<String> headings = this.deserializer.deserialize(csv).get(0).keySet();
		assertThat(headings, equalTo(Set.of("first", "second")));
	}

	@Test
	public void headingsAreKeptInOrder() {
		String csv = "second,first\na,b";
		Set<String> headings = this.deserializer.deserialize(csv).get(0).keySet();
		assertThat(headings, equalTo(Set.of("second", "first")));
	}

	@Test
	public void headingsAreKeys() {
		String csv = "heading1,heading2\nv1,v2";
		Set<String> headings = this.deserializer.deserialize(csv).get(0).keySet();
		assertThat(headings, equalTo(Set.of("heading1", "heading2")));
	}

	@Test
	public void everyMapHasSameKeys() {
		String csv = "h1,h2\nr1h1,r1h2\nr2h1,r2h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps.get(0).keySet(), equalTo(maps.get(1).keySet()));
	}

	@Test
	public void headingsCannotBeBlank() {
		String invalidCsv = "h1,\na,b";
		List<Map<String, String>> maps = this.deserializer.deserialize(invalidCsv);
		assertThat(maps.size(), is(0));
	}

	@Test
	public void headingsNameMustBeUnique() {
		String invalidCsv = "duplicate,unique,duplicate\na,b,c\nd,e,f";
		List<Map<String, String>> maps = this.deserializer.deserialize(invalidCsv);
		assertThat(maps.size(), is(0));
	}

	@Test
	public void valuesAreCommaSeparated() {
		String csv = "first,second\na,b";
		Collection<String> values = this.deserializer.deserialize(csv).get(0).values();
		assertThat(List.copyOf(values), equalTo(List.of("a", "b")));
	}

	@Test
	public void oneMapPerValuesRow_singleRow() {
		String csv = "h1,h2\nr1h1,r1h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps.size(), is(1));
	}

	@Test
	public void oneMapPerValuesRow_severalRows() {
		String csv = "h1,h2\nr1h1,r1h2\nr2h1,r2h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps.size(), is(2));
	}

	@Test
	public void invalidLinesAreIgnored_missingValues() {
		String csv = "h1,h2\ninvalid-line\nr2h1,r2h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps.size(), is(1));
	}

	@Test
	public void invalidLinesAreIgnored_extraValues() {
		String csv = "h1,h2\nr1h1,r1h2,r1h3\nr2h1,r2h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(csv);
		assertThat(maps.size(), is(1));
	}

	@Test
	public void emptyValuesAreKept() {
		String invalidCsv = "h1,h2\nr1h1,\n,r2h2";
		List<Map<String, String>> maps = this.deserializer.deserialize(invalidCsv);
		assertThat(maps, equalTo(List.of(
			Map.of("h1", "r1h1", "h2", ""),
			Map.of("h1", "", "h2", "r2h2"))));
	}

	@Test
	public void noValues_emptyList() {
		String emptyCsv = "h1,h2\n";
		List<Map<String, String>> maps = this.deserializer.deserialize(emptyCsv);
		assertThat(maps.size(), is(0));
	}
}
