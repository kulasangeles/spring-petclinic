/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/**
 * Integration Test for {@link CrashController}.
 *
 * @author Alex Lutz
 */
// NOT Waiting https://github.com/spring-projects/spring-boot/issues/5574
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "server.error.include-message=ALWAYS", "management.endpoints.enabled-by-default=false" })
class CrashControllerIntegrationTests {

	@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	static class TestConfiguration {

	}

	@Value(value = "${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate rest;

	@Test
	void testTriggerExceptionJson() {
		ResponseEntity<Map<String, Object>> resp = rest.exchange(
				RequestEntity.get("http://localhost:" + port + "/oups").build(),
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
		Assertions.assertThat(resp).isNotNull();
		Assertions.assertThat(resp.getStatusCode().is5xxServerError());
		Assertions.assertThat(resp.getBody().containsKey("timestamp"));
		Assertions.assertThat(resp.getBody().containsKey("status"));
		Assertions.assertThat(resp.getBody().containsKey("error"));
		Assertions.assertThat(resp.getBody()).containsEntry("message",
				"Expected: controller used to showcase what happens when an exception is thrown");
		Assertions.assertThat(resp.getBody()).containsEntry("path", "/oups");
	}

	@Test
	void testTriggerExceptionHtml() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.TEXT_HTML));
		ResponseEntity<String> resp = rest.exchange("http://localhost:" + port + "/oups", HttpMethod.GET,
				new HttpEntity<>(headers), String.class);
		Assertions.assertThat(resp).isNotNull();
		Assertions.assertThat(resp.getStatusCode().is5xxServerError());
		Assertions.assertThat(resp.getBody()).isNotNull();
		// html:
		Assertions.assertThat(resp.getBody()).containsSubsequence("<body>", "<h2>", "Something happened...", "</h2>", "<p>",
				"Expected:", "controller", "used", "to", "showcase", "what", "happens", "when", "an", "exception", "is",
				"thrown", "</p>", "</body>");
		// Not the whitelabel error page:
		Assertions.assertThat(resp.getBody()).doesNotContain("Whitelabel Error Page",
				"This application has no explicit mapping for");
	}

}
