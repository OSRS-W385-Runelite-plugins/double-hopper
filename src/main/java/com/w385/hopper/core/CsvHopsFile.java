package com.w385.hopper.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.stream.Collectors.*;

/**
 * Writes and reads {@link Hop hops} from a CSV file
 */
public final class CsvHopsFile {

	private final CsvCodec codec;

	private final HopMapper mapper;

	private static final Charset CHARSET = US_ASCII;

	private static final String LINE_SEPARATOR = "\n";

	public CsvHopsFile(CsvCodec codec, HopMapper mapper) {
		this.codec = codec;
		this.mapper = mapper;
	}

	/**
	 * Inserts a {@link Hop hop} at the end of the file
	 * If this is the first insertion, headings are also added
	 *
	 * @param file - the {@link Path path} of the file to write to
	 * @param hop - the {@link Hop hop} to insert
	 *
	 * @throws IOException - if the file couldn't be read or writen
	 */
	public void add(Path file, Hop hop) throws IOException {
		Map<String, String> map = this.mapper.toMap(hop);
		if (isEmpty(file))
			appendHeadings(file, map.keySet());
		appendValues(file, map);
	}

	/**
	 * Returns all the {@link Hop hops} stored in the file
	 *
	 * @param file - the {@link Path path} to the file to read
	 *
	 * @return - every {@link Hop hop} stored in the file
	 *
	 * @throws IOException - if the file couldn't be read
	 */
	public List<Hop> all(Path file) throws IOException {
		return existingMaps(file).stream()
			.map(this.mapper::toObject)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(toList());
	}

	/**
	 * Returns the last {@link Hop hop} stored in the file
	 *
	 * @param file - the {@link Path path} to the file to read
	 *
	 * @return - the last {@link Hop hop} stored in the file
	 *
	 * @throws IOException - if the file couldn't be read
	 */
	public Optional<Hop> last(Path file) throws IOException {
		List<Hop> hops = all(file);
		if (hops.isEmpty())
			return Optional.empty();
		return Optional.of(hops.get(hops.size() - 1));
	}

	/**
	 * Returns the content of the file, as a single String
	 *
	 * @param file - the {@link Path path} to the file to read
	 *
	 * @return - the whole file, as a single String
	 *
	 * @throws IOException - if the file couldn't be read
	 */
	private String content(Path file) throws IOException {
		return Files.readString(file, CHARSET);
	}

	/**
	 * Returns entries of the file, as maps
	 *
	 * @param file - the {@link Path file} to the file to read
	 *
	 * @return - stored entries, as maps
	 *
	 * @throws IOException - if the file couldn't be read
	 */
	private List<Map<String, String>> existingMaps(Path file) throws IOException {
		return this.codec.deserialize(content(file));
	}

	/**
	 * Checks whether the file already contains content
	 *
	 * @param file - the {@link Path path} to the file to read
	 *
	 * @return - true if the file is empty, false otherwise
	 *
	 * @throws IOException - if the file couldn't be read
	 */
	private boolean isEmpty(Path file) throws IOException {
		return Files.size(file) == 0;
	}

	/**
	 * Appends content at the end of the file
	 *
	 * @param file - the {@link Path path} to the file to append content to
	 * @param content - the content to append
	 *
	 * @throws IOException - if the file couldn't be writen
	 */
	private void appendFileContent(Path file, String content) throws IOException {
		Files.writeString(file, content, CHARSET, APPEND);
	}

	/**
	 * Appends headings at the end of the file, which should be empty
	 *
	 * @param file - the {@link Path path} to the file to append headings to
	 * @param headings - the headings to append to the file
	 *
	 * @throws IOException - if the file couldn't be writen
	 */
	private void appendHeadings(Path file, Set<String> headings) throws IOException {
		// must preserve iteration order
		var map = new LinkedHashMap<String, String>();

		for (String key : headings)
			map.put(key, key);

		appendFileContent(file, this.codec.serialize(map));
	}

	/**
	 * Appends values at the end of the file
	 *
	 * @param file - the {@link Path path} to the file to append values to
	 * @param map - the map to get values from
	 *
	 * @throws IOException - if the file couldn't be writen
	 */
	private void appendValues(Path file, Map<String, String> map) throws IOException {
		appendFileContent(file, LINE_SEPARATOR + this.codec.serialize(map));
	}
}
