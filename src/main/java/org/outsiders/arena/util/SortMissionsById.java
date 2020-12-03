package org.outsiders.arena.util;

import java.util.Comparator;

import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Mission;

public class SortMissionsById implements Comparator<Mission> {
	
	public int compare(Mission a, Mission b) {
		return a.getId() - b.getId();
	}
}
