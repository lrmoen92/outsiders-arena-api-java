package org.outsiders.arena.domain;

import java.util.List;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Table
public class Mission {

	@PrimaryKey
	private int id;
	
	private String name;
	private String description;
	private String avatarUrl;
	private int minmumLevel;
	private int prerequisiteMissionId;
	private int characterIdUnlocked;
	private List<MissionRequirement> requirements;
	
	
	public int getId() {
		return id;
	}
	public void setId(int missionId) {
		this.id = missionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public int getMinmumLevel() {
		return minmumLevel;
	}
	public void setMinmumLevel(int minmumLevel) {
		this.minmumLevel = minmumLevel;
	}
	public int getPrerequisiteMissionId() {
		return prerequisiteMissionId;
	}
	public void setPrerequisiteMissionId(int prerequisiteMissionId) {
		this.prerequisiteMissionId = prerequisiteMissionId;
	}
	public int getCharacterIdUnlocked() {
		return characterIdUnlocked;
	}
	public void setCharacterIdUnlocked(int characterIdUnlocked) {
		this.characterIdUnlocked = characterIdUnlocked;
	}
	public List<MissionRequirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<MissionRequirement> requirements) {
		this.requirements = requirements;
	}
	
	
	
}
