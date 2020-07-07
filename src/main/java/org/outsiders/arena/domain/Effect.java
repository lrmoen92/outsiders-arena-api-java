package org.outsiders.arena.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("battleeffect")
public class Effect
{
  
  protected String name;
  // blurb
  protected String description;
  protected String avatarUrl = "https://i.imgur.com/CiUI6Sg.png";

  protected int duration = 1;
  // TODO: list of conditions, list of qualities....  yeah it's ultimately better can't deny it.
  // conditional string to meet
  protected String condition;
  // buff or debuff
  protected String quality;
  protected boolean interruptable = false;
  protected boolean physical = false;
  protected boolean magical = false;
  protected boolean affliction = false;
  protected boolean conditional = false;
  protected boolean visible = true;
  
  // we MAYYYY not need this.  but i'm keeping it for now.
  protected boolean stacks = false;
  protected Map<String, Integer> statMods = Collections.emptyMap();
  
  public Effect() {}
  
  public Effect(boolean physical, boolean magical, boolean affliction, boolean interruptable, boolean conditional)
  {
    this.physical = physical;
    this.magical = magical;
    this.affliction = affliction;
    this.interruptable = interruptable;
    this.conditional = conditional;
  }
  
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
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
