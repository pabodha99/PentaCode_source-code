package com.pentacode.pentacode_middleware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Date;

@SpringBootApplication
@EnableAsync
public class PentacodeMiddlewareApplication {

	private static Logger audit = LogManager.getLogger("audit-log");

	public static void main(String[] args) {
		audit.info("pentacode-middleware,at," + new Date() + ",start,ok");
		SpringApplication.run(PentacodeMiddlewareApplication.class, args);
	}

}
