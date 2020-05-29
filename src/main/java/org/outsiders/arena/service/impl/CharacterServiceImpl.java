package org.outsiders.arena.service.impl;

import java.util.Optional;

import org.outsiders.arena.domain.Character;
import org.outsiders.arena.repository.CharacterRepository;
import org.outsiders.arena.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterServiceImpl
  implements CharacterService
{
  private CharacterRepository repo;
  
  @Autowired
  public CharacterServiceImpl(CharacterRepository repo)
  {
    this.repo = repo;
  }
  
  public Character save(Character entity)
  {
    return (Character)this.repo.save(entity);
  }
  
  public Iterable<Character> saveAll(Iterable<Character> entities)
  {
    return this.repo.saveAll(entities);
  }
  
  public Optional<Character> findById(Integer id)
  {
    return this.repo.findById(id);
  }
  
  public boolean existsById(Integer id)
  {
    return this.repo.existsById(id);
  }
  
  public Iterable<Character> findAll()
  {
    return this.repo.findAll();
  }
  
  public Iterable<Character> findAllById(Iterable<Integer> ids)
  {
    return this.repo.findAllById(ids);
  }
  
  public long count()
  {
    return this.repo.count();
  }
  
  public void deleteById(Integer id)
  {
    this.repo.deleteById(id);
  }
  
  public void delete(Character entity)
  {
    this.repo.delete(entity);
  }
  
  public void deleteAll(Iterable<Character> entities)
  {
    this.repo.deleteAll(entities);
  }
  
  public void deleteAll()
  {
    this.repo.deleteAll();
  }
}
