package org.outsiders.arena.domain;

import java.util.List;

public class CostCheckDTO {
	
	List<AbilityTargetDTO> chosenAbilities;
	List<List<String>> allyCosts;
	
	public List<AbilityTargetDTO> getChosenAbilities() {
		return chosenAbilities;
	}
	public void setChosenAbilities(List<AbilityTargetDTO> chosenAbilities) {
		this.chosenAbilities = chosenAbilities;
	}
	public List<List<String>> getAllyCosts() {
		return allyCosts;
	}
	public void setAllyCosts(List<List<String>> allyCosts) {
		this.allyCosts = allyCosts;
	}
	

}
