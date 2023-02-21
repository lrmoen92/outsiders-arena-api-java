package org.outsiders.arena.service;

import java.util.List;
import java.util.Optional;

import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Combatant;

public abstract interface CharacterService
{
  public abstract Combatant save(Combatant paramCharacter);
  
  public abstract Combatant findById(Integer paramInteger);
    
  public abstract List<Combatant> findAll();
  
}
