package org.outsiders.arena.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.lang.Character;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
@Table(name = "MissionRequirement", schema = "outsiders")
@Embeddable
public class MissionRequirement
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
	private int amount;
	
	private String userFaction = "ANYONE";
	private String targetFaction = "ANYONE";
	
    public MissionRequirement() {
    	
    }
    
    public MissionRequirement(int amount, MissionRequirement parent) {
    	this.amount = amount;
    	this.userFaction = parent.getUserFaction();
    	this.targetFaction = parent.getTargetFaction();
    }
	
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
}
