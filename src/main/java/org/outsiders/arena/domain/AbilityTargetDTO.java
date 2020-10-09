package org.outsiders.arena.domain;

import java.util.List;

public class AbilityTargetDTO {
	
	// all positional
	Ability ability;
	Integer characterPosition;
	List<Integer> targetPositions;
	
	public Ability getAbility() {
		return ability;
	}
	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	public Integer getCharacterPosition() {
		return characterPosition;
	}
	public void setCharacterPosition(Integer characterPosition) {
		this.characterPosition = characterPosition;
	}
	public List<Integer> getTargetPositions() {
		return targetPositions;
	}
	public void setTargetPositions(List<Integer> targetPositions) {
		this.targetPositions = targetPositions;
	}

}
