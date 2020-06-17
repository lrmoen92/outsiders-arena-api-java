package org.outsiders.arena.domain;

public class Conditional {

	
	// this all seems out of scope ONLY BOTTOM METHOD REALLY MATTERS
//	//deals any damage
//	public static final String ATTACK = "ATTACK";
//	//gains armor, shields, or invulnerability
//	public static final String DEFEND = "DEFEND";
//	//do nothing
//	public static final String HOLD = "HOLD";
//	//target an ally
//	public static final String BUFF = "BUFF";
//	//target an enemy non-damaging
//	public static final String DEBUFF = "DEBUFF";
//	//take any damage
//	public static final String DAMAGED = "DAMAGED";
//	//become buffed by ally
//	public static final String BUFFED = "BUFFED";
//	//become targeted by an enemy
//	public static final String TARGETED = "TARGETED";
//	  // get a kill
//	  public static final String KILLER = "KILLER";
//	  // get killed
//	  public static final String KILLED = "KILLED";
	
//	  // affected by ability or effect_name
	  public static String AFFECTED_BY(String effectName) {
		  return "AFFECTED_BY_" + effectName.toUpperCase(); 
	  };
	  // affected by quality/status
	  public static String HAS_QUALITY(String qualityName) {
		  return "IS_" + qualityName.toUpperCase();
	  };
	
}
