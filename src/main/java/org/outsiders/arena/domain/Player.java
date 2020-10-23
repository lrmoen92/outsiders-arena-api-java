package org.outsiders.arena.domain;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Player
{
  @PrimaryKey
  private int id;
  private String avatarUrl;
  private String displayName;
  private PlayerCredentials credentials;
  // rank
  private int level;
  // raw xp (lp)
  private int xp;
  private List<Integer> missionIdsCompleted;
  private List<Integer> characterIdsUnlocked;
  // mission id, current amount (as opposed to total amount needed)
  private List<MissionProgress> missionProgress;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }

  public String getAvatarUrl()
  {
    return this.avatarUrl;
  }
  
  public void setAvatarUrl(String avatarUrl)
  {
    this.avatarUrl = avatarUrl;
  }
  
  public int getLevel()
  {
    return this.level;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
  public String getDisplayName()
  {
    return this.displayName;
  }
  
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

	@Override
	public String toString() {
		return "Player [id=" + id + ", " + (avatarUrl != null ? "avatarUrl=" + avatarUrl + ", " : "")
				+ (displayName != null ? "displayName=" + displayName + ", " : "") + "level=" + level + "]";
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public List<Integer> getCharacterIdsUnlocked() {
		return characterIdsUnlocked;
	}

	public void setCharacterIdsUnlocked(List<Integer> characterIdsUnlocked) {
		this.characterIdsUnlocked = characterIdsUnlocked;
	}

	public List<Integer> getMissionIdsCompleted() {
		return missionIdsCompleted;
	}

	public void setMissionIdsCompleted(List<Integer> missionIdsCompleted) {
		this.missionIdsCompleted = missionIdsCompleted;
	}

	public PlayerCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(PlayerCredentials credentials) {
		this.credentials = credentials;
	}

	public List<MissionProgress> getMissionProgress() {
		return missionProgress;
	}

	public void setMissionProgress(List<MissionProgress> missionProgress) {
		this.missionProgress = missionProgress;
	}

}
