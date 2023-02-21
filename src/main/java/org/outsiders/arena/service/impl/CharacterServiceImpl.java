package org.outsiders.arena.service.impl;

import java.util.List;

import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Combatant;
import org.outsiders.arena.repository.Characters;
import org.outsiders.arena.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterServiceImpl implements CharacterService {
  
  @Autowired
  Characters characters;
  
  public Combatant save(Combatant entity)
  {
    return this.characters.addCharacter(entity);
  }

  public Combatant findById(Integer id)
  {
    return this.characters.getCharacter(id);
  }
  
  public List<Combatant> findAll()
  {
    return this.characters.getAll();
  }

}
