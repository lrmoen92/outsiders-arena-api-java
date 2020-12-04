package org.outsiders.arena.domain;

import java.util.Arrays;
import java.util.List;
import java.lang.Character;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import com.fasterxml.jackson.annotation.JsonGetter;

@UserDefinedType
public class MissionRequirement {
	
	private int amount;
	
	private int missionId;
	
	private String userFaction;
	private String targetFaction;
	
	@JsonGetter
	public String getDescription() {
		StringBuilder result = new StringBuilder();
		List<Character> vowels = Arrays.asList('A', 'E', 'I', 'O', 'U');
		
		result.append("Win " + amount + " game");
		if (amount != 1) {
			result.append("s");
		}
		
		if (userFaction != null) {
			char one = userFaction.toCharArray()[0];
			String an = "a ";
			if(vowels.contains(one)) {
				an = "an ";
			}
			
			result.append(" with " + an + userFaction);
		}
		
		if (targetFaction != null) {
			char one = targetFaction.toCharArray()[0];
			String an = "a ";
			if(vowels.contains(one)) {
				an = "an ";
			}
			
			result.append(" vs " + an + targetFaction);
		}
		
		return result.toString();
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getUserFaction() {
		return userFaction;
	}
	public void setUserFaction(String userFaction) {
		this.userFaction = userFaction;
	}
	public String getTargetFaction() {
		return targetFaction;
	}
	public void setTargetFaction(String targetFaction) {
		this.targetFaction = targetFaction;
	}

	public int getMissionId() {
		return missionId;
	}

	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	
}
