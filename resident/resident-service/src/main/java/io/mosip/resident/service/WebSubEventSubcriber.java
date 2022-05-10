package io.mosip.resident.service;

import java.util.function.Supplier;

public interface WebSubEventSubcriber {
	
	/**
	 * subscribe.
	 *
	 * @param enableTester the enable tester
	 */
	void subscribe(Supplier<Boolean> enableTester);
	
}
