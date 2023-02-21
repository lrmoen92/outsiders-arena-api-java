package org.outsiders.arena.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

@Entity
public class BattleEffect extends Effect {

	// used to identify an effect within the context of a battle
	// set during battle logic (random number i guess, just not negative)
	private int instanceId;
	// set during battle logic for groups of effects (random number i guess, just
	// not negative)
	private int groupId;
	// only for effects on character instances (should be position based)
	private int originCharacter;
	private int targetCharacter;

	public BattleEffect() {
	}

	public BattleEffect(boolean physical, boolean magical, boolean affliction, boolean interruptable,
			boolean conditional) {
		this.physical = physical;
		this.magical = magical;
		this.affliction = affliction;
		this.interruptable = interruptable;
		this.conditional = conditional;
	}

	public String toString() {
		return "Name: " + this.name + ", Dur: " + this.duration + ", InstID: " + this.instanceId + ", Desc: "
				+ this.description;
	}

	public BattleEffect(BattleEffect e) {
		this.duration = e.duration;
		this.avatarUrl = e.avatarUrl;
		this.instanceId = e.instanceId;
		this.name = e.name;
		this.condition = e.condition;
		this.quality = e.quality;
		this.description = e.description;
		this.originCharacter = e.originCharacter;
		this.targetCharacter = e.targetCharacter;
		this.interruptable = e.interruptable;
		this.physical = e.physical;
		this.magical = e.magical;
		this.affliction = e.affliction;
		this.conditional = e.conditional;
		this.visible = e.visible;
		this.stacks = e.stacks;
		this.statMods = new HashMap<>();
		for (Map.Entry<String, Integer> entry : e.getStatMods().entrySet()) {
			this.statMods.put(entry.getKey(), entry.getValue());
		}
	}

	public void triggerAndRevealCounter(Ability abilityCountered) {
		this.setVisible(true);
		this.setDuration(995);
		this.setDescription(abilityCountered.getName() + " has been countered.");
		this.setQuality(null);
		this.setCondition(null);
		this.setConditional(false);
		this.setStatMods(null);
		this.setStacks(false);
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getOriginCharacter() {
		return originCharacter;
	}

	public void setOriginCharacter(int originCharacter) {
		this.originCharacter = originCharacter;
	}

	public int getTargetCharacter() {
		return targetCharacter;
	}

	public void setTargetCharacter(int targetCharacter) {
		this.targetCharacter = targetCharacter;
	}

}
