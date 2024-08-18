/*
 * This source file was generated by the Gradle 'init' task
 */
package net.tegulis.template;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class LibraryTest {

	@Test
	void messageIsGenerated() {
		String inputName = "world";
		String expectedOutput = "Hello world!";
		String actualOutput = Library.generateMessage(inputName);
		assertThat(actualOutput).isEqualTo(expectedOutput);
	}

}
