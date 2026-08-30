package com.w385.hopper.core;

/**
 * An hop status
 */
public enum Status {
	/**
	 * Player logged in
	 */
	SUCCESS,

	/**
	 * Connection failed (e.g., network error, already logged in, limit reached)
	 */
	FAILURE
}
