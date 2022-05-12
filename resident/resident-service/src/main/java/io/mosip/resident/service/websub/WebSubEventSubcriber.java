package io.mosip.resident.service.websub;

import java.util.function.Supplier;

/**
 * The Interface WebSubEventSubcriber.
 */
public interface WebSubEventSubcriber {
	
	/**
	 * subscribe.
	 *
	 * @param enableTester the enable tester
	 */
	void subscribe(Supplier<Boolean> enableTester);
	
}
