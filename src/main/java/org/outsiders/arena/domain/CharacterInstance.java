package org.outsiders.arena.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class CharacterInstance
{
  private int hp = 100;
  private int cooldownOne = 0;
  private int cooldownTwo = 0;
  private int cooldownThree = 0;
  private int cooldownFour = 0;
  // player 1 (0, 1, 2) player 2 (3, 4, 5)
  private int position;
  private int characterId;
  private List<Effect> effects = Collections.emptyList();
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
	    } else if (hp < 0) {
	        this.hp = 0;
	        this.setDead(true);
	    } else {
	        this.hp = hp;
	    }
	  }

  }
  
  public List<Effect> getEffects()
  {
    return this.effects;
  }
  
  public void setEffects(List<Effect> effects)
  {
    this.effects = effects;
  }

	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public int getCooldownOne() {
		return cooldownOne;
	}

	public void setCooldownOne(int cooldownOne) {
		this.cooldownOne = cooldownOne;
	}

	public int getCooldownTwo() {
		return cooldownTwo;
	}

	public void setCooldownTwo(int cooldownTwo) {
		this.cooldownTwo = cooldownTwo;
	}

	public int getCooldownThree() {
		return cooldownThree;
	}

	public void setCooldownThree(int cooldownThree) {
		this.cooldownThree = cooldownThree;
	}

	public int getCooldownFour() {
		return cooldownFour;
	}

	public void setCooldownFour(int cooldownFour) {
		this.cooldownFour = cooldownFour;
	}
}
