package org.outsiders.arena.domain;

import java.util.List;

public class CostCheckDTO {
	
	private List<AbilityTargetDTO> chosenAbilities;
	private List<List<String>> allyCosts;
	private List<String> spentEnergy;
	
	
	
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
	public List<String> getSpentEnergy() {
		return spentEnergy;
	}
	public void setSpentEnergy(List<String> spentEnergy) {
		this.spentEnergy = spentEnergy;
	}
	

}
