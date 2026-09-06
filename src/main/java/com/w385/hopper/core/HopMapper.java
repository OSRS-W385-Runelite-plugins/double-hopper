package com.w385.hopper.core;

import java.time.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Maps hops from and to key-value format
 */
public final class HopMapper {

	/**
	 * The key for the account field
	 */
	private static final String ACCOUNT_KEY = "account";

	/**
	 * The key for the instant field
	 */
	private static final String DATETIME_KEY = "timestamp";

	/**
	 * The key for the status field
	 */
	private static final String STATUS_KEY = "status";

	/**
	 * The key for the source world field
	 */
	private static final String SOURCE_WORLD_KEY = "from_world";

	/**
	 * The key for the destination world field
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
		Integer destinationWorld = destinationWorldFromString(hop.get(DESTINATION_WORLD_KEY));

		// factory can't be called without causing NPE
		if (destinationWorld == null)
			return Optional.empty();

		return Hop.create(
			hop.get(ACCOUNT_KEY),
			timestampFromString(hop.get(DATETIME_KEY)),
			statusFromString(hop.get(STATUS_KEY)),
			sourceWorldFromString(hop.get(SOURCE_WORLD_KEY)),
			destinationWorld);
	}

	/**
	 * Turns the given hop to a map
	 *
	 * @param hop - the hop to map
	 *
	 * @return - the created map
	 */
	public Map<String, String> toMap(Hop hop) {
		return Map.of(
			ACCOUNT_KEY, hop.account,
			DATETIME_KEY, String.valueOf(hop.instant.getEpochSecond()),
			STATUS_KEY, hop.status.name(),
			SOURCE_WORLD_KEY, Objects.toString(hop.fromWorld, ""),
			DESTINATION_WORLD_KEY, String.valueOf(hop.toWorld));
	}

	/**
	 * Converts the timestamp into a LocalDateTime
	 *
	 * @param timestamp - the String containing the timestamp
	 *
	 * @return - the timestamp as a LocalDateTime, if transformation cannot
	 * 	be done, returns null
	 */
	private static Instant timestampFromString(String timestamp) {
		if (timestamp == null)
			return null;
		if (! timestamp.matches("\\d+"))
			return null;

		long epoch = Long.parseLong(timestamp);
		return Instant.ofEpochSecond(epoch);
	}

	/**
	 * Converts the status to an enum member
	 *
	 * @param status - the String containing the status
	 *
	 * @return - the status as an enum member, if transformation cannot
	 * 	be done, returns null
	 */
	private static Status statusFromString(String status) {
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
	private static Integer sourceWorldFromString(String sourceWorld) {
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
	private static Integer destinationWorldFromString(String destinationWorld) {
		if (destinationWorld == null)
			return null;
		if (destinationWorld.isBlank())
			return null;
		if (! destinationWorld.matches("\\d+"))
			return null;
		return Integer.parseInt(destinationWorld);
	}
}
