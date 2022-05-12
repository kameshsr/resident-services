package io.mosip.resident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(scanBasePackages = { "io.mosip.resident.*", "io.mosip.kernel.core.*",
		"io.mosip.kernel.dataaccess.hibernate.*", "io.mosip.kernel.crypto.jce.*", "io.mosip.commons.packet.*",
		"io.mosip.kernel.keygenerator.bouncycastle.*", "${mosip.auth.adapter.impl.basepackage}",
		"io.mosip.kernel.virusscanner.*", "io.mosip.commons.khazana.*", "io.mosip.kernel.websub.api.client.*"})

@ComponentScan(basePackages = { "io.mosip.kernel.websub.api.client.SubscriberClientImpl.class"}, excludeFilters =
		{
				@ComponentScan.Filter(type = FilterType.REGEX, pattern = { "io.mosip.kernel.websub.api.filter.*" })
		})
public class ResidentBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResidentBootApplication.class, args);
	}

}
