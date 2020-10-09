package org.outsiders.arena.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class CharacterInstance
{
  private int hp = 100;
  private List<Integer> cooldowns = Arrays.asList(0, 0, 0, 0);
  // player 1 (0, 1, 2) player 2 (3, 4, 5)
  private int position;
  private int characterId;
  // EffectHolder class is built like this map essentially maybe even better :O
  
  private List<BattleEffect> effects = new ArrayList<>();
  // store conditional flags here
  private List<String> flags = new ArrayList<>();
  private boolean dead = false;
  
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
