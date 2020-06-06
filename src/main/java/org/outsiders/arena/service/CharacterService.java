package org.outsiders.arena.service;

import java.util.List;
import java.util.Optional;

import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.Character;

public abstract interface CharacterService
{
  public abstract Character save(Character paramCharacter);
  
  public abstract Iterable<Character> saveAll(Iterable<Character> paramIterable);
  
  public abstract Optional<Character> findById(Integer paramInteger);
  
  public abstract boolean existsById(Integer paramInteger);
  
  public abstract List<Character> getCharactersForBattle(Battle battle);
  
  public abstract Iterable<Character> findAll();
  
  public abstract Iterable<Character> findAllById(Iterable<Integer> paramIterable);
  
  public abstract long count();
  
  public abstract void deleteById(Integer paramInteger);
  
  public abstract void delete(Character paramCharacter);
  
  public abstract void deleteAll(Iterable<Character> paramIterable);
  
  public abstract void deleteAll();
}
