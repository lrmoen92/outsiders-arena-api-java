package org.outsiders.arena.util;

import java.util.Comparator;

import org.outsiders.arena.domain.Character;

public class SortCharactersById implements Comparator<Character> {
	
	public int compare(Character a, Character b) {
		return a.getId() - b.getId();
	}
}
