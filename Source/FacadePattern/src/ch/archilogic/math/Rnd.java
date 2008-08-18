package ch.archilogic.math;

import java.util.Random;

import ch.archilogic.log.Logger;

public class Rnd {
	private static Random rnd = null;
	
	public static void init() {
		rnd = new Random(32479827);
	}
	
	public static double nextDouble() {
		double r = rnd.nextDouble();
		Logger.debug(String.format("random: %f", r));
		return r;
	}
}
