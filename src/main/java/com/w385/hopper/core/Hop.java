package com.w385.hopper.core;

import java.time.*;
import java.util.Optional;

/**
 * A player's connection attempt to a server
 */
public final class Hop {
	/**
	 * Some unique ID for the account (account_hash being unavailable on failed login)
	 */
	public final String account;

	/**
	 * When the attempt happened
	 */
	public final LocalDateTime datetime;

	/**
	 * The status, @see Status
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

	private Hop(String account, LocalDateTime datetime, Status status, Integer fromWorld, int toWorld) {
		this.account = account;
		this.datetime = datetime;
		this.status = status;
		this.fromWorld = fromWorld;
		this.toWorld = toWorld;
	}

	/**
	 * Factory, applies domain logic to decide if it's worth instantiating
	 */
	public static Optional<Hop> create(String account, LocalDateTime datetime, Status status, Integer fromWorld, int toWorld) {
		if (account == null || account.isBlank())
			return Optional.empty();
		if (datetime == null || isTooOld(datetime))
			return Optional.empty();
		if (status == null)
			return Optional.empty();
		if (fromWorld != null && (fromWorld < 0 || fromWorld == toWorld))
			return Optional.empty();
		if (toWorld < 0)
			return Optional.empty();

		return Optional.of(new Hop(account, datetime, status, fromWorld, toWorld));
	}

	/**
	 * Checks if the given datetime is obsolete
	 *
	 * @param datetime - the time to check
	 *
	 * @return - true if the datetime is too old to be relevant, false otherwise
	 */
	private static boolean isTooOld(LocalDateTime datetime) {
		ZoneId timezone = ZoneId.systemDefault();
		return datetime.isBefore(LocalDateTime.now(timezone).minusHours(4));
	}
}
