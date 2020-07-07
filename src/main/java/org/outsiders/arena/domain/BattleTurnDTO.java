package org.outsiders.arena.domain;

import java.util.List;
import java.util.Map;

public class BattleTurnDTO
{
	// list of spent energy
	Map<String, Integer> spentEnergy;

	// list of all effects by id?
	List<BattleEffect> effects;
  
	// list of abilityID -> list of targetIDs (all positional ie:2nd char ability 3 is abilityID 6)
	List<AbilityTargetDTO> abilities;

	public List<BattleEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<BattleEffect> effects) {
		this.effects = effects;
	}

	public List<AbilityTargetDTO> getAbilities() {
		return abilities;
	}

	public void setAbilities(List<AbilityTargetDTO> abilities) {
		this.abilities = abilities;
	}

	public Map<String, Integer> getSpentEnergy() {
		return spentEnergy;
	}

	public void setSpentEnergy(Map<String, Integer> spentEnergy) {
		this.spentEnergy = spentEnergy;
	}
	
}
