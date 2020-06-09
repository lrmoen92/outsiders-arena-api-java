package org.outsiders.arena.domain;

import java.util.List;

public class AbilityTargetDTO {
	
	// all positional
	Ability ability;
	CharacterInstance character;
	List<Integer> targetPositions;
	
	public Ability getAbility() {
		return ability;
	}
	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	public List<Integer> getTargets() {
		return targetPositions;
	}
	public void setTargets(List<Integer> targets) {
		this.targetPositions = targets;
	}

}
