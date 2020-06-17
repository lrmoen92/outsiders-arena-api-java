package org.outsiders.arena.domain;

// basically status effects
public class Quality
{
  // has active hidden effect
  public static final String STEALTHED = "STEALTHED";
  // hidden skills used by or on character revealed
  public static final String REVEALED = "REVEALED";
  // costs increased (+1 random)
  public static final String DISARMED = "DISARMED";
  // costs decreased (-1 random)
  public static final String EQUIPPED = "EQUIPPED";
//  // this might be affected by
//  public static final String MARKED = "MARKED";
  // immune to dmg
  public static final String INVULNERABLE = "INVULNERABLE";
  // cant reduce dmg or go invuln
  public static final String VULNERABLE = "VULNERABLE";
  // immune to targets
  public static final String UNTARGETABLE = "UNTARGETABLE";
  // immune to being immune to targets
  public static final String TARGETABLE = "TARGETABLE";
  // is stunned all skills
  public static final String STUNNED = "STUNNED";
  // is stunned physical skills
  public static final String PHYSICAL_STUNNED = "PHYSICAL_STUNNED";
  // is stunned magic skills
  public static final String MAGICAL_STUNNED = "MAGICAL_STUNNED";
  // get enemy effect removed
  public static final String DISPELLED = "DISPELLED";
  // get healed
  public static final String RESTORED = "RESTORED";
  // affected by ability or effect_name
  public static String AFFECTED_BY(String effectName) {
	  return "AFFECTED_" + effectName.toUpperCase(); 
  };
}
