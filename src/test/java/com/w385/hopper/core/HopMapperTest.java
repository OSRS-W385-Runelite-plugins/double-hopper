package com.w385.hopper.core;

import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static com.w385.hopper.core.Status.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HopMapperTest {

	private final HopMapper mapper = new HopMapper();

	private final Instant now = Instant.now();

	private final String timestamp = String.valueOf(now.getEpochSecond());

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
		Map<String, String> map = map("account", timestamp, null, "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void statusCannotBeBlank() {
		Map<String, String> map = map("account", timestamp, "", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void statusMustBeValidEnumMember() {
		Map<String, String> map = map("account", timestamp, "ERROR", "1", "2");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void sourceWorldIsIgnoredIfNull() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", null, "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void sourceWorldIsIgnoredIfBlank() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", "", "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void sourceWorldMustBeIntegerIfProvided() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", "error", "2");
		Hop hop = mapper.toObject(map).orElseThrow();
		assertThat(hop.fromWorld, is(nullValue()));
	}

	@Test
	public void destinationWorldCannotBeNull() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", "1", null);
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void destinationWorldCannotBeBlank() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", "1", "");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void destinationWorldMustBeInteger() {
		Map<String, String> map = map("account", timestamp, "SUCCESS", "1", "error");
		Optional<Hop> hop = mapper.toObject(map);
		assertThat(hop, is(Optional.empty()));
	}

	@Test
	public void mapHasAccountKey() {
		Hop hop = Hop.create("account", now, SUCCESS, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.keySet(), hasItem("account"));
	}

	@Test
	public void mapHasCorrectAccount() {
		Hop hop = Hop.create("correct", now, SUCCESS, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("account"), is("correct"));
	}

	@Test
	public void mapHasTimestampKey() {
		Hop hop = Hop.create("account", now, SUCCESS, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.keySet(), hasItem("timestamp"));
	}

	@Test
	public void mapHasCorrectTimestamp() {
		Hop hop = Hop.create("account", now, SUCCESS, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("timestamp"), is(timestamp));
	}

	@Test
	public void mapHasStatusKey() {
		Hop hop = Hop.create("account", now, FAILURE, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.keySet(), hasItem("status"));
	}

	@Test
	public void mapHasCorrectStatus() {
		Hop hop = Hop.create("account", now, FAILURE, 1, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("status"), is("FAILURE"));
	}

	@Test
	public void mapHasSourceWorldKey() {
		Hop hop = Hop.create("account", now, SUCCESS, 42, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.keySet(), hasItem("from_world"));
	}

	@Test
	public void mapHasCorrectSourceWorld() {
		Hop hop = Hop.create("account", now, SUCCESS, 42, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("from_world"), is("42"));
	}

	@Test
	public void mapHasBlankSourceWorldWhenNone() {
		Hop hop = Hop.create("account", now, SUCCESS, null, 2).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("from_world"), is(""));
	}

	@Test
	public void mapHasDestinationWorldKey() {
		Hop hop = Hop.create("account", now, SUCCESS, 1, 1337).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.keySet(), hasItem("to_world"));
	}

	@Test
	public void mapHasCorrectDestinationWorld() {
		Hop hop = Hop.create("account", now, SUCCESS, 1, 1337).orElseThrow();
		Map<String, String> map = mapper.toMap(hop);
		assertThat(map.get("to_world"), is("1337"));
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
