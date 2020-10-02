package org.outsiders.arena.domain;

import java.util.List;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Character
{
  @PrimaryKey
  private int id;
  private String avatarUrl;
  // use name-contains for multiple "alex"s for example
  private String name;
  private String description;
  private List<String> factions;
  private Ability slot1;
  private Ability slot2;
  private Ability slot3;
  private Ability slot4;
  
  // THIS IS FOR FUTURE STATE, WE GOTTA GET RID OF THIS 1234 SHIT
//  private List<Ability> abilities;
  
  
  public String toString()
  {
	  
	  return "Name: " + this.name + ", Id: " + this.id;
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getAvatarUrl()
  {
    return this.avatarUrl;
  }
  
  public void setAvatarUrl(String avatarUrl)
  {
    this.avatarUrl = avatarUrl;
  }
  
  public Ability getSlot1()
  {
    return this.slot1;
  }
  
  public void setSlot1(Ability slot1)
  {
    this.slot1 = slot1;
  }
  
  public Ability getSlot2()
  {
    return this.slot2;
  }
  
  public void setSlot2(Ability slot2)
  {
    this.slot2 = slot2;
  }
  
  public Ability getSlot3()
  {
    return this.slot3;
  }
  
  public void setSlot3(Ability slot3)
  {
    this.slot3 = slot3;
  }
  
  public Ability getSlot4()
  {
    return this.slot4;
  }
  
  public void setSlot4(Ability slot4)
  {
    this.slot4 = slot4;
  }


public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public List<String> getFactions() {
	return factions;
}

public void setFactions(List<String> factions) {
	this.factions = factions;
}
}
