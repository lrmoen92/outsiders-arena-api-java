package org.outsiders.arena.domain;

public class Conditional {

	//deals any damage
	public static final String ATTACK = "ATTACK";
	//gains armor, shields, or invulnerability
	public static final String DEFEND = "DEFEND";
	//do nothing
	public static final String HOLD = "HOLD";
	//target an ally
	public static final String BUFF = "BUFF";
	//target an enemy non-damaging
	public static final String DEBUFF = "DEBUFF";
	//take any damage
	public static final String DAMAGED = "DAMAGED";
	//become buffed by ally
	public static final String BUFFED = "BUFFED";
	//become targeted by an enemy
	public static final String TARGETED = "TARGETED";
	
	public static String EFFECTED(String abilityName) {
		return "EFFECTED_" + abilityName.toUpperCase();
	}
}
