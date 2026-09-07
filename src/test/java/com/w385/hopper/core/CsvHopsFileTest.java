package com.w385.hopper.core;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static com.w385.hopper.core.Status.*;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CsvHopsFileTest {

	private final CsvCodec codec = new CsvCodec();

	private final HopMapper mapper = new HopMapper();

	private final CsvHopsFile hops = new CsvHopsFile(codec, mapper);

	private static final Charset CHARSET = US_ASCII;

	@ClassRule
	public static TemporaryFolder workingDirectory = new TemporaryFolder();

	@Test
	public void firstInsertionPutsHeadingsAndValues() throws IOException {
		Path path = temporaryFile();
		this.hops.add(path, randomHop());
		assertThat(Files.readAllLines(path, CHARSET).size(), is(2));
	}

	@Test
	// FIXME: iteration order is currently defined by a seed set at JVM startup
	// 		-> consistent, but differs between JVM / processes
	public void valuesAreInsertedInCorrespondingColumns() throws IOException {
		// given: a hop to store
		Path path = temporaryFile();
		Hop hop = randomHop();

		// when: storing it
		this.hops.add(path, hop);

		// then: values should be inserted in corresponding columns
		String csv = Files.readString(path,CHARSET);
		Map<String, String> storedMap = this.codec.deserialize(csv).get(0);
		assertThat(storedMap, is(this.mapper.toMap(hop)));
	}

	@Test
	public void nextInsertionsPutValuesOnly() throws IOException {
		Path path = temporaryFileWithContent(randomHop());
		this.hops.add(path, randomHop());
		assertThat(Files.readAllLines(path, CHARSET).size(), is(3));
	}

	@Test
	public void allReturnsAllEntries() throws IOException {
		Path path = temporaryFileWithContent(randomHops(7));
		List<Hop> hops = this.hops.all(path);
		assertThat(hops.size(), is(7));
	}

	@Test
	public void lastReturnsLastInsertedEntry() throws IOException {
		// given: several hops in the file
		Path path = temporaryFileWithContent(randomHops(10));
		Hop hop = Hop.create("last-inserted", Instant.now(), SUCCESS, 301, 302)
			.orElseThrow();
		this.hops.add(path, hop);

		// when: retrieving the last Hop
		Hop last = this.hops.last(path).orElseThrow();

		// then: it should be the one inserted last
		assertThat(last, is(hop));
	}

	/**
	 * Creates a new file inside the working directory
	 *
	 * @return - the path to the created file
	 */
	private static Path temporaryFile() throws IOException {
		return workingDirectory.newFile().toPath();
	}

	/**
	 * Creates a new file inside the working directory
	 *
	 * @return - the path to the created file
	 */
	private Path temporaryFileWithContent(Hop ...hops) throws IOException {
		Path path = workingDirectory.newFile().toPath();
		List<Map<String, String>> maps = Arrays.stream(hops)
			.map(this.mapper::toMap)
			.collect(toList());
		String csv  = this.codec.serialize(maps);
		Files.writeString(path, csv, CHARSET);
		return path;
	}

	/**
	 * Creates an account name as a random lowercase String
	 *
	 * @return - the generated account name
	 */
	private static String randomAccount() {
		return new Random().ints('a', 'z' + 1)
			.limit(8)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}

	/**
	 * Creates a random int in the specified inclusive range
	 *
	 * @param min - the lower bound
	 * @param max - the upper bound
	 *
	 * @return - the generated int
	 */
	private static int randomInt(int min, int max) {
		return new Random().ints(min, max + 1)
			.limit(1)
			.reduce(0, Integer::sum);
	}

	/**
	 * Generates a random instant in the last 4 hours range
	 *
	 * @return - the generated instant
	 */
	private static Instant randomInstant() {
		int fourHoursAgoInSeconds = 60 * 60 * 4;
		int secondsAgo = randomInt(0, fourHoursAgoInSeconds);
		return Instant.now().minusSeconds(secondsAgo);
	}

	/**
	 * Generates a Hop with random values
	 *
	 * @return - the generated Hop
	 */
	private static Hop randomHop() {
		Integer fromWorld = randomInt(301, 400);
		int toWorld = randomInt(301, 400);

		// must differ, collisions transformed into "from login screen"
		if (fromWorld == toWorld)
			fromWorld = null;

		return Hop.create(
			randomAccount(),
			randomInstant(),
			Status.values()[randomInt(0, 1)],
			fromWorld,
			toWorld).orElseThrow();
	}

	/**
	 * Generates Hops with random values
	 *
	 * @param count - the number of Hops to generate
	 *
	 * @return - the generated Hops
	 */
	private static Hop[] randomHops(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> randomHop())
			.toArray(Hop[]::new);
	}
}
