package net.tegulis.template;

public class Main {

	public static String VERSION = "1.0.0";

	public static void main(String[] ignored) {
		System.out.println("Main version " + VERSION);
		System.out.println(Library.generateMessage("world"));
	}

}
