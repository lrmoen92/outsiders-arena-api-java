package org.outsiders.arena.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Player
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  
  private String avatarUrl;
  
  @Column(unique=true)
  private String displayName;
  
  @OneToOne(cascade = CascadeType.ALL)
  private PlayerCredentials credentials;
  
  // rank
  private int level;
  
  // raw xp (lp)
  // 0/100  (+30, +20, +10) (-25, -15, -5)
  private int xp;
  
  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private Set<Integer> missionIdsCompleted;
  
  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private Set<Integer> characterIdsUnlocked;
  
  // mission id, current amount (as opposed to total amount needed)
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<MissionProgress> missionProgress;
  
  @JsonIgnore
  private int loseXP(int i) {
	  int x = this.getXp();
	  
	  int res = x - i;
	  
	  if (res < 0) {
		  // demote
		  res = 100 - Math.abs(res);
		  int l = this.getLevel();
		  if (l > 1) {
			  this.setLevel(l - 1);
		  } else {
			  this.setXp(0);
		  }
	  } else {
		  this.setXp(res);
	  }
	  
	  return i;
  }
  
  @JsonIgnore
  private int gainXP(int i) {
	  int x = this.getXp();
	  
	  int res = x + i;
	  
	  if (res > 99) {
		  // rankup
		  res = res - 100;
		  int l = this.getLevel();
		  this.setLevel(l + 1);
		  this.setXp(res);
	  } else {
		  this.setXp(res);
	  }
	  
	  return i;
  }
  
  @JsonIgnore
  public int loseBattleXP(Player opponent) {
	  if (opponent.getLevel() > this.getLevel()) {
		  return this.loseXP(5);
		  // lose low xp
	  } else if (opponent.getLevel() < this.getLevel()) {
		  return this.loseXP(25);
		  // lose high xp
	  } else {
		  return this.loseXP(15);
		  // lose moderate xp
	  }
  }
  
  @JsonIgnore
  public int winBattleXP(Player opponent) {
	  if (opponent.getLevel() > this.getLevel()) {
		  return this.gainXP(30);
		  // reward high xp
	  } else if (opponent.getLevel() < this.getLevel()) {
		  return this.gainXP(10);
		  // reward low xp
	  } else {
		  return this.gainXP(20);
		  // reward moderate xp
	  }
  }
  
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

	public Set<Integer> getCharacterIdsUnlocked() {
		return characterIdsUnlocked;
	}

	public void setCharacterIdsUnlocked(Set<Integer> characterIdsUnlocked) {
		this.characterIdsUnlocked = characterIdsUnlocked;
	}

	public Set<Integer> getMissionIdsCompleted() {
		return missionIdsCompleted;
	}

	public void setMissionIdsCompleted(Set<Integer> missionIdsCompleted) {
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
