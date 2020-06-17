package org.outsiders.arena.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
public class Effect
{
  private int duration = 1;
  private String avatarUrl = "https://i.imgur.com/CiUI6Sg.png";
  // used to identify an effect within the context of a battle
  // set during battle logic (random number i guess, just not negative)
  private int instanceId;
  private String name;
  // conditional string to meet
  private String condition;
  // buff or debuff
  private String quality;
  // blurb
  private String description;
  // only for effects on character instances (should be position based)
  private int originCharacter;
  private int targetCharacter;
  private boolean interruptable = false;
  private boolean physical = false;
  private boolean magical = false;
  private boolean affliction = false;
  private boolean conditional = false;
  private boolean visible = true;
  
  // we MAYYYY not need this.  but i'm keeping it for now.
  private boolean stacks = false;
  private Map<String, Integer> statMods = Collections.emptyMap();
  
  public Effect() {}
  
  public Effect(Effect e) {
	  this.duration = e.duration;
	  this.avatarUrl = e.avatarUrl;
	  this.instanceId = e.instanceId;
	  this.name = e.name;
	  this.condition = e.condition;
	  this.quality = e.quality;
	  this.description = e.description;
	  this.originCharacter = e.originCharacter;
	  this.targetCharacter = e.targetCharacter;
	  this.interruptable = e.interruptable;
	  this.physical = e.physical;
	  this.magical = e.magical;
	  this.affliction = e.affliction;
	  this.conditional = e.conditional;
	  this.visible = e.visible;
	  this.stacks = e.stacks;
	  this.statMods = new HashMap<>();
	  for(Map.Entry<String, Integer> entry : e.getStatMods().entrySet()) {
		  this.statMods.put(entry.getKey(), entry.getValue());
	  }
  }
  
  public Effect(boolean physical, boolean magical, boolean affliction, boolean interruptable, boolean conditional)
  {
    this.physical = physical;
    this.magical = magical;
    this.affliction = affliction;
    this.interruptable = interruptable;
    this.conditional = conditional;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }

	public boolean isConditional() {
		return conditional;
	}
	
	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}
	
	public void setInterruptable(boolean interruptable)
  {
    this.interruptable = interruptable;
  }
  
  public void setPhysical(boolean physical)
  {
    this.physical = physical;
  }
  
  public void setMagical(boolean magical)
  {
    this.magical = magical;
  }
  
  public int getDuration()
  {
    return this.duration;
  }
  
  public void setDuration(int duration)
  {
    this.duration = duration;
  }
  
  public boolean isInterruptable()
  {
    return this.interruptable;
  }
  
  public boolean isPhysical()
  {
    return this.physical;
  }
  
  public boolean isMagical()
  {
    return this.magical;
  }
  
  
  
  public Map<String, Integer> getStatMods()
  {
    return this.statMods;
  }
  
  public void setStatMods(Map<String, Integer> statMods)
  {
    this.statMods = statMods;
  }



public int getOriginCharacter() {
	return originCharacter;
}

public void setOriginCharacter(int originCharacter) {
	this.originCharacter = originCharacter;
}

public int getTargetCharacter() {
	return targetCharacter;
}

public void setTargetCharacter(int targetCharacter) {
	this.targetCharacter = targetCharacter;
}

public String getCondition() {
	return condition;
}

public void setCondition(String condition) {
	this.condition = condition;
}

public String getQuality() {
	return quality;
}

public void setQuality(String quality) {
	this.quality = quality;
}

public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}

public int getInstanceId() {
	return instanceId;
}

public void setInstanceId(int instanceId) {
	this.instanceId = instanceId;
}

public String getAvatarUrl() {
	return avatarUrl;
}

public void setAvatarUrl(String avatarUrl) {
	this.avatarUrl = avatarUrl;
}

public boolean isAffliction() {
	return affliction;
}

public void setAffliction(boolean affliction) {
	this.affliction = affliction;
}

public boolean isStacks() {
	return stacks;
}

public void setStacks(boolean stacks) {
	this.stacks = stacks;
}
}
