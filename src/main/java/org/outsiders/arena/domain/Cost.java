package org.outsiders.arena.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Cost {

	  public static final List<String> free = Collections.emptyList();
	  public static final List<String> oneRan = Collections.singletonList(Energy.RANDOM);
	  public static final List<String> oneDiv = Collections.singletonList(Energy.DIVINITY);
	  public static final List<String> oneArc = Collections.singletonList(Energy.ARCANA);
	  public static final List<String> oneStr = Collections.singletonList(Energy.STRENGTH);
	  public static final List<String> oneDex = Collections.singletonList(Energy.DEXTERITY);
	  public static final List<String> oneDivOneRan = Arrays.asList(Energy.DIVINITY, Energy.RANDOM);
	  public static final List<String> oneArcOneRan = Arrays.asList(Energy.ARCANA, Energy.RANDOM);
	  public static final List<String> oneStrOneRan = Arrays.asList(Energy.STRENGTH, Energy.RANDOM);
	  public static final List<String> oneDexOneRan = Arrays.asList(Energy.DEXTERITY, Energy.RANDOM);
	  public static final List<String> twoRan = Arrays.asList(Energy.RANDOM, Energy.RANDOM);
	  public static final List<String> twoDiv = Arrays.asList(Energy.DIVINITY, Energy.DIVINITY);
	  public static final List<String> twoArc = Arrays.asList(Energy.ARCANA, Energy.ARCANA);
	  public static final List<String> twoStr = Arrays.asList(Energy.STRENGTH, Energy.STRENGTH);
	  public static final List<String> twoDex = Arrays.asList(Energy.DEXTERITY, Energy.DEXTERITY);
	
}
