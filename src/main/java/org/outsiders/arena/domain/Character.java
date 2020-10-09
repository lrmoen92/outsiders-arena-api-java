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
  private List<Ability> abilities;
  
  
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

public List<Ability> getAbilities() {
	return abilities;
}

public void setAbilities(List<Ability> abilities) {
	this.abilities = abilities;
}
}
