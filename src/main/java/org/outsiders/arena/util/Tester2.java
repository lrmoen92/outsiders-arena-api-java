package org.outsiders.arena.util;

public class Tester2 extends Tester {
	
	public static void main() {
		String a = "string";
		String b = new String("string");
		String c = a;
		
		System.out.println(a == b);
		System.out.println(a == c);
		System.out.println(b.equals(c));
	}
}
