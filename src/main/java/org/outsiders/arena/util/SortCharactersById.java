package org.outsiders.arena.util;

import java.util.Comparator;

import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Combatant;

public class SortCharactersById implements Comparator<Combatant> {
	
	public int compare(Combatant a, Combatant b) {
		return a.getCharacterId() - b.getCharacterId();
	}
}
