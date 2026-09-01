package com.w385.hopper.core;

import java.time.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Maps hops from and to key-value format
 */
public final class HopMapper {
	/**
	 * The name of the account column in the CSV
	 */
	private static final String ACCOUNT_KEY = "account";

	/**
	 * The name of the datetime column in the CSV
	 */
	private static final String DATETIME_KEY = "timestamp";

	/**
	 * The name of the status column in the CSV
	 */
	private static final String STATUS_KEY = "status";

	/**
	 * The name of the source world column in the CSV
	 */
	private static final String SOURCE_WORLD_KEY = "from_world";

	/**
	 * The name of the destination world column in the CSV
	 */
	private static final String DESTINATION_WORLD_KEY = "to_world";

	/**
	 * Turns the given map to a usable business object
	 * Domain validation is enforced by the called factory
	 *
	 * @param hop - the map with appropriate keys
	 *
	 * @return - the result of the called constructor, or null if primitive values
	 * 	couldn't be transformed
	 */
	public Optional<Hop> toObject(Map<String, String> hop) {
		String account = hop.get(ACCOUNT_KEY);
		LocalDateTime timestamp = transformTimestamp(hop.get(DATETIME_KEY));
		Status status = transformStatus(hop.get(STATUS_KEY));
		Integer sourceWorld = transformSourceWorld(hop.get(SOURCE_WORLD_KEY));
		Integer destinationWorld = transformDestinationWorld(hop.get(DESTINATION_WORLD_KEY));

		// constructor can't be called without causing NPE
		if (destinationWorld == null)
			return Optional.empty();

		return Hop.create(account, timestamp, status, sourceWorld, destinationWorld);
	}

	/**
	 * Converts the timestamp into a LocalDateTime
	 *
	 * @param timestamp - the String containing the timestamp
	 *
	 * @return - the timestamp as a LocalDateTime, if transformation cannot
	 * 	be done, returns null
	 */
	private static LocalDateTime transformTimestamp(String timestamp) {
		if (timestamp == null)
			return null;
		if (! timestamp.matches("\\d+"))
			return null;

		long epoch = Long.parseLong(timestamp);
		Instant instant = Instant.ofEpochSecond(epoch);

		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	/**
	 * Converts the status to an enum member
	 *
	 * @param status - the String containing the status
	 *
	 * @return - the status as an enum member, if transformation cannot
	 * 	be done, returns null
	 */
	private static Status transformStatus(String status) {
		if (status == null)
			return null;

		List<String> validStatus = Arrays.stream(Status.values())
			.map(Status::name)
			.collect(toList());

		if (validStatus.contains(status))
			return Status.valueOf(status);

		return null;
	}

	/**
	 * Converts the source world to a nullable Integer
	 *
	 * @param sourceWorld - the String containing the source world
	 *
	 * @return - the source world as a nullable Integer, if transformation
	 *  cannot be done, returns null
	 */
	private static Integer transformSourceWorld(String sourceWorld) {
		if (sourceWorld == null)
			return null;
		if (sourceWorld.isBlank())
			return null;
		if (! sourceWorld.matches("\\d+"))
			return null;
		return Integer.parseInt(sourceWorld);
	}

	/**
	 * Converts the destination world to a primitive int
	 *
	 * @param destinationWorld - the String containing the destination world
	 *
	 * @return - the destination world as a primitive int, if transformation
	 *  cannot be done, returns null
	 */
	private static Integer transformDestinationWorld(String destinationWorld) {
		if (destinationWorld == null)
			return null;
		if (destinationWorld.isBlank())
			return null;
		if (! destinationWorld.matches("\\d+"))
			return null;
		return Integer.parseInt(destinationWorld);
	}
}
