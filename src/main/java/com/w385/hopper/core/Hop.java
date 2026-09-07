package com.w385.hopper.core;

import java.time.*;
import java.util.*;

/**
 * A player's connection attempt to a server
 */
public final class Hop {

	/**
	 * Some unique ID for the account
	 */
	public final String account;

	/**
	 * When the attempt happened
	 */
	public final Instant instant;

	/**
	 * The {@link Status status}, i.e. did the connection succeed
	 */
	public final Status status;

	/**
	 * On which world the player was, null for login screen
	 */
	public final Integer fromWorld;

	/**
	 * On which world the player tried to hop
	 */
	public final int toWorld;

	private Hop(String account, Instant instant, Status status, Integer fromWorld, int toWorld) {
		this.account = account;
		this.instant = instant;
		this.status = status;
		this.fromWorld = fromWorld;
		this.toWorld = toWorld;
	}

	/**
	 * Factory, applies domain logic to decide if it's valid and worth instantiating
	 *
	 * @param account - a unique ID for the account, must not be blank
	 * @param instant - when did the attempt happened, millis and nanos will be ignored
	 * @param status - the status of the attempt
	 * @param fromWorld - the world the player was on, null in case of login screen
	 * @param toWorld - the world the player wanted to go to, must differ from fromWorld
	 *
	 * @return - the created Hop, or none if validation failed
	 */
	public static Optional<Hop> create(String account, Instant instant, Status status, Integer fromWorld, int toWorld) {
		if (account == null || account.isBlank())
			return Optional.empty();
		if (instant == null)
			return Optional.empty();
		if (status == null)
			return Optional.empty();
		if (fromWorld != null && (fromWorld < 0 || fromWorld == toWorld))
			return Optional.empty();
		if (toWorld < 0)
			return Optional.empty();

		Instant epoch = instant.minusNanos(instant.getNano());
		return Optional.of(new Hop(account, epoch, status, fromWorld, toWorld));
	}

	@Override
	public String toString() {
		return String.format(
			"Hop {account:\"%s\", epoch:%d, status:%s, from:%s, to:%d}",
			this.account,
			this.instant.getEpochSecond(),
			this.status.name(),
			this.fromWorld,
			this.toWorld);
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, instant, status, fromWorld, toWorld);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (! (other instanceof Hop))
			return false;
		return equals((Hop) other);
	}

	public boolean equals(Hop other) {
		return Objects.equals(this.account, other.account)
			&& Objects.equals(this.instant, other.instant)
			&& Objects.equals(this.status, other.status)
			&& Objects.equals(this.fromWorld, other.fromWorld)
			&& Objects.equals(this.toWorld, other.toWorld);
	}
}
