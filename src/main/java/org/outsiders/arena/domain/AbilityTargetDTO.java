package org.outsiders.arena.domain;

import java.util.List;

public class AbilityTargetDTO {
	
	// all positional
	Ability ability;
	List<Integer> targets;
	
	public Ability getAbility() {
		return ability;
	}
	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	public List<Integer> getTargets() {
		return targets;
	}
	public void setTargets(List<Integer> targets) {
		this.targets = targets;
	}

}
