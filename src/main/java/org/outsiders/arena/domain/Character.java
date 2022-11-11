package org.outsiders.arena.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
@Table(name = "Character", schema = "outsiders")
public class Character
{
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String avatarUrl;
  private String name;
  private String description;
  
  @ElementCollection(fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  private List<String> factions;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
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
