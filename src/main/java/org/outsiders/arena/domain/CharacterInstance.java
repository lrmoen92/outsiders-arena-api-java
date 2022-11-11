package org.outsiders.arena.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name = "CharacterInstance", schema = "outsiders")
@Embeddable
public class CharacterInstance
{	  
	@Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
  private int hp = 100;
  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<Integer> cooldowns = new ArrayList<>(4);
  // player 1 (0, 1, 2) player 2 (3, 4, 5)
  private int position;
  private int characterId;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<BattleEffect> effects = new ArrayList<>();
  // store conditional flags here

  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<String> flags = new ArrayList<>();
  private boolean dead = false;
  
  public CharacterInstance() {
	  this.cooldowns.add(0);
	  this.cooldowns.add(0);
	  this.cooldowns.add(0);
	  this.cooldowns.add(0);
  }
  
  public boolean isDead()
  {
    return this.dead;
  }
  
  public void setDead(boolean dead)
  {
    this.dead = dead;
  }
  
  public int getCharacterId()
  {
    return this.characterId;
  }
  
  public void setCharacterId(int characterId)
  {
    this.characterId = characterId;
  }
  
  public int getHp()
  {
    return this.hp;
  }
  
  public void setHp(int hp)
  {
	  if (!this.isDead()) {
	    if (hp > 100) {
	        this.hp = 100;
	    } else if (hp <= 0) {
	        this.hp = 0;
	        this.setDead(true);
	    } else {
	        this.hp = hp;
	    }
	  }

  }
  
  
  
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public List<BattleEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<BattleEffect> effects) {
		this.effects = effects;
	}

	public List<String> getFlags() {
		return flags;
	}

	public void setFlags(List<String> flags) {
		this.flags = flags;
	}

	public List<Integer> getCooldowns() {
		return cooldowns;
	}

	public void setCooldowns(List<Integer> cooldowns) {
		this.cooldowns = cooldowns;
	}

}
