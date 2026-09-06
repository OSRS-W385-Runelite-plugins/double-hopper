package com.w385.hopper.core;

import org.junit.Test;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HopTest {

	private final Instant now = Instant.now();

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
		Instant instant = null;
		Optional<Hop> hop = Hop.create("account", instant, Status.SUCCESS, 1, 2);
		assertTrue(hop.isEmpty());
	}

	@Test
	public void hopsWhichHappenedLessThan4HoursAgoAreCreated() {
		Instant instant = now.minusSeconds(60L * 60L * 4L - 5);
		Optional<Hop> hop = Hop.create("account", instant, Status.SUCCESS, 1, 2);
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

	@Test
	public void instantHasSecondsPrecision() {
		Hop hop = Hop.create("account", Instant.now(), Status.SUCCESS, 301, 414).orElseThrow();
		long milliAndNanoSeconds = hop.instant.getNano();
		assertThat(milliAndNanoSeconds, is(0L));
	}

	@Test
	public void notEqualsNull() {
		Hop hop = Hop.create("hop", now, Status.SUCCESS, 301, 302).orElseThrow();
		boolean equality = hop.equals((Object) null);
		assertThat(equality, is(false));
	}

	@Test
	public void equalsItself() {
		Hop hop = Hop.create("hop", now, Status.SUCCESS, 301, 302).orElseThrow();
		boolean equality = hop.equals((Object) hop);
		assertThat(equality, is(true));
	}

	@Test
	public void notEqualsOtherClass() {
		Hop hop = Hop.create("hop", now, Status.SUCCESS, 301, 302).orElseThrow();
		boolean equality = hop.equals(this);
		assertThat(equality, is(false));
	}

	@Test
	public void differentAccountsNotEqual() {
		Hop first = Hop.create("first", now, Status.SUCCESS, 301, 302).orElseThrow();
		Hop second = Hop.create("second", now, Status.SUCCESS, 301, 302).orElseThrow();
		assertThat(first, not(equalTo(second)));
	}

	@Test
	public void differentInstantsNotEqual() {
		Hop first = Hop.create("same", now, Status.SUCCESS, 301, 302).orElseThrow();
		Hop second = Hop.create("same", now.minusSeconds(1), Status.SUCCESS, 301, 302).orElseThrow();
		assertThat(first, not(equalTo(second)));
	}

	@Test
	public void differentStatusNotEqual() {
		Hop first = Hop.create("same", now, Status.SUCCESS, 301, 302).orElseThrow();
		Hop second = Hop.create("same", now, Status.FAILURE, 301, 302).orElseThrow();
		assertThat(first, not(equalTo(second)));
	}

	@Test
	public void differentSourceWorldsNotEqual() {
		Hop first = Hop.create("same", now, Status.SUCCESS, 400, 302).orElseThrow();
		Hop second = Hop.create("same", now, Status.FAILURE, 577, 302).orElseThrow();
		assertThat(first, not(equalTo(second)));
	}

	@Test
	public void differentDestinationWorldsNotEqual() {
		Hop first = Hop.create("same", now, Status.SUCCESS, 301, 414).orElseThrow();
		Hop second = Hop.create("same", now, Status.FAILURE, 301, 326).orElseThrow();
		assertThat(first, not(equalTo(second)));
	}

	@Test
	public void sameFieldsEqual() {
		Hop first = Hop.create("same", now, Status.SUCCESS, 301, 302).orElseThrow();
		Hop second = Hop.create("same", now, Status.SUCCESS, 301, 302).orElseThrow();
		assertThat(first, equalTo(second));
	}
}
