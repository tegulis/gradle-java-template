package net.tegulis.template;

public final class Library {

	static String generateMessage(String name) {
		return "Hello %s!".formatted(name);
	}

}
