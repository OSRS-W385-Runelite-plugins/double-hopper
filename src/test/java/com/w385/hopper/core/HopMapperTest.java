package com.w385.hopper.core;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HopMapperTest {

	private final HopMapper mapper = new HopMapper();

	private final String now = String.valueOf(
		Instant.now()
			.minus(1, ChronoUnit.MINUTES)
			.getEpochSecond());

	@Test
	public void timestampCannotBeNull() {
		Map<String, String> map = map("account", null, "SUCCESS", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void timestampCannotBeBlank() {
		Map<String, String> map = map("account", "", "SUCCESS", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void timestampMustBeInteger() {
		Map<String, String> map = map("account", "error", "SUCCESS", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void timestampMustBePositiveInteger() {
		Map<String, String> map = map("account", "-1", "SUCCESS", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void statusCannotBeNull() {
		Map<String, String> map = map("account", now, null, "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void statusCannotBeBlank() {
		Map<String, String> map = map("account", now, "", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void statusMustBeValidEnumMember() {
		Map<String, String> map = map("account", now, "ERROR", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void sourceWorldIsIgnoredIfNull() {
		Map<String, String> map = map("account", now, "SUCCESS", null, "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void sourceWorldIsIgnoredIfBlank() {
		Map<String, String> map = map("account", now, "SUCCESS", "", "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void sourceWorldMustBeIntegerIfProvided() {
		Map<String, String> map = map("account", now, "SUCCESS", "error", "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void destinationWorldCannotBeNull() {
		Map<String, String> map = map("account", now, "SUCCESS", "1", null);
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void destinationWorldCannotBeBlank() {
		Map<String, String> map = map("account", now, "SUCCESS", "1", "");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void destinationWorldMustBeInteger() {
		Map<String, String> map = map("account", now, "SUCCESS", "1", "error");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	private Map<String, String> map(String account, String timestamp, String status, String from, String to) {
		return new HashMap<>() {{
			put("account", account);
			put("timestamp", timestamp);
			put("status", status);
			put("from_world", from);
			put("to_world", to);
		}};
	}
}
