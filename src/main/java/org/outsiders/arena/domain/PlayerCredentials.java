package org.outsiders.arena.domain;

import java.io.Serializable;

public class PlayerCredentials implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8895585864692900053L;
	
	private static String email;
	private static String password;
	
	public static String getEmail() {
		return email;
	}
	public static void setEmail(String email) {
		PlayerCredentials.email = email;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		PlayerCredentials.password = password;
	}

}
