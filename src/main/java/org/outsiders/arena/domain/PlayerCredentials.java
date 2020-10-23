package org.outsiders.arena.domain;

import java.io.Serializable;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class PlayerCredentials implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8895585864692900053L;
	
	private String email;
	private String password;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
