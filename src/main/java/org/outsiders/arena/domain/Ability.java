package org.outsiders.arena.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class Ability
{
  private int cooldown = 0;
  private String name;
  private String abilityUrl;
  private String description;
  private List<String> cost = Collections.singletonList("RANDOM");
  private List<Effect> selfEffects = Collections.emptyList();
  private List<Effect> enemyEffects = Collections.emptyList();
  private List<Effect> aoeEnemyEffects = Collections.emptyList();
  private List<Effect> allyEffects = Collections.emptyList();
  private List<Effect> aoeAllyEffects = Collections.emptyList();
  private boolean aoe = false;
  private boolean self = false;
  private boolean ally = false;
  private boolean enemy = false;
  
  public Ability() {}
  
  public Ability(boolean enemies, boolean allies, boolean self, boolean aoe)
  {
    this.enemy = enemies;
    this.ally = allies;
    this.self = self;
    this.aoe = aoe;
  }
  
  
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  
  
  public int getCooldown()
  {
    return this.cooldown;
  }
  
  public void setCooldown(int cooldown)
  {
    this.cooldown = cooldown;
  }
  
  public List<String> getCost()
  {
    return this.cost;
  }
  
  public void setCost(List<String> cost)
  {
    this.cost = cost;
  }
  
  public List<Effect> getAllyEffects() 
  {
	  return this.allyEffects; 
  }
  
  public void setAllyEffects(List<Effect> allyEffects)
  {
    this.allyEffects = allyEffects;
  }
  
  public boolean isAlly()
  {
    return this.ally;
  }
  
  public void setAlly(boolean ally)
  {
    this.ally = ally;
  }
  
  public boolean isEnemy()
  {
    return this.enemy;
  }
  
  public void setEnemy(boolean enemy)
  {
    this.enemy = enemy;
  }
  
  public List<Effect> getSelfEffects()
  {
    return this.selfEffects;
  }
  
  public void setSelfEffects(List<Effect> selfEffects)
  {
    this.selfEffects = selfEffects;
  }
  
  public List<Effect> getAoeEnemyEffects()
  {
    return this.aoeEnemyEffects;
  }
  
  public void setAoeEnemyEffects(List<Effect> aoeEnemyEffects)
  {
    this.aoeEnemyEffects = aoeEnemyEffects;
  }
  
  public List<Effect> getAoeAlliedEffects()
  {
    return this.aoeAllyEffects;
  }
  
  public void setAoeAlliedEffects(List<Effect> aoeAlliedEffects)
  {
    this.aoeAllyEffects = aoeAlliedEffects;
  }
  
  public void setAoe(boolean aoe)
  {
    this.aoe = aoe;
  }
  
  public void setSelf(boolean self)
  {
    this.self = self;
  }
  
  public boolean isAoe()
  {
    return this.aoe;
  }
  
  public boolean isSelf()
  {
    return this.self;
  }

public String getAbilityUrl() {
	return abilityUrl;
}

public void setAbilityUrl(String abilityUrl) {
	this.abilityUrl = abilityUrl;
}

public List<Effect> getEnemyEffects() {
	return enemyEffects;
}

public void setEnemyEffects(List<Effect> enemyEffects) {
	this.enemyEffects = enemyEffects;
}

public List<Effect> getAoeAllyEffects() {
	return aoeAllyEffects;
}

public void setAoeAllyEffects(List<Effect> aoeAllyEffects) {
	this.aoeAllyEffects = aoeAllyEffects;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}
}
