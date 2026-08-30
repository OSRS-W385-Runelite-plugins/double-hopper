package com.w385.hopper.core;

import static org.junit.Assert.*;
import org.junit.Test;

import java.time.*;
import java.util.*;

public class HopTest {

	private final ZoneId timezone = ZoneId.systemDefault();

	LocalDateTime now = LocalDateTime.now(timezone);

	@Test
	public void accountCannotBeNull() {
		String account = null;
		Optional<Hop> hop = Hop.create(account, now, Status.SUCCESS, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void accountCannotBeBlank() {
		String account = "";
		Optional<Hop> hop = Hop.create(account, now, Status.SUCCESS, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void datetimeCannotBeNull() {
		LocalDateTime datetime = null;
		Optional<Hop> hop = Hop.create("account", datetime, Status.SUCCESS, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void hopsWhichHappenedMoreThan4HoursAgoAreIgnored() {
		LocalDateTime datetime = now.minusHours(4).minusSeconds(5);
		Optional<Hop> hop = Hop.create("account", datetime, Status.SUCCESS, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void hopsWhichHappenedLessThan4HoursAgoAreCreated() {
		LocalDateTime datetime = now.minusHours(4).plusSeconds(5);
		Optional<Hop> hop = Hop.create("account", datetime, Status.SUCCESS, 1, 2);
		assertTrue(hop.isPresent());
	}

	@Test
	public void statusCannotBeNull() {
		Status status = null;
		Optional<Hop> hop = Hop.create("account", now, status, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void sourceWorldMustBePositiveIfProvided() {
		int source = -1;
		Optional<Hop> hop = Hop.create("account", now, Status.SUCCESS, source, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void destinationWorldMustBePositive() {
		int destination = -2;
		Optional<Hop> hop = Hop.create("account", now, Status.SUCCESS, 1, destination);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void sourceAndDestinationWorldsMustBeDifferent() {
		int source = 301, destination = 301;
		Optional<Hop> hop = Hop.create("account", now, Status.SUCCESS, source, destination);
		assertTrue(hop.isEmpty());
	}
}
