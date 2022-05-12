package io.mosip.resident.service.websub;

import java.util.function.Supplier;

/**
 * The Interface WebSubEventTopicRegistrar.
 */
public interface WebSubEventTopicRegistrar {
	
	/**
	 * Initialize.
	 *
	 * @param enableTester the enable tester
	 */
	void register(Supplier<Boolean> enableTester);
	
}
