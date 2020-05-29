package org.outsiders.arena.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class CharacterInstance
{
  private int hp = 100;
  // player 1 (0, 1, 2) player 2 (3, 4, 5)
  private int position;
  private int characterId;
  private List<Effect> effects = Collections.emptyList();
  private boolean dead = false;
  private boolean highlighted = false;
  
  public boolean isHighlighted()
  {
    return this.highlighted;
  }
  
  public void setHighlighted(boolean highlighted)
  {
    this.highlighted = highlighted;
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
    if (hp > 100) {
      this.hp = 100;
    } else {
      this.hp = hp;
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
}
