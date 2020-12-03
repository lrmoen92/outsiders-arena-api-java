package org.outsiders.arena.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.Mission;
import org.outsiders.arena.domain.CharacterInstance;
import org.outsiders.arena.repository.CharacterRepository;
import org.outsiders.arena.repository.MissionRepository;
import org.outsiders.arena.service.CharacterService;
import org.outsiders.arena.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MissionServiceImpl
  implements MissionService
{
  private MissionRepository repo;
  
  @Autowired
  public MissionServiceImpl(MissionRepository repo)
  {
    this.repo = repo;
  }
  
  public Mission save(Mission entity)
  {
    return (Mission)this.repo.save(entity);
  }
  
  public Iterable<Mission> saveAll(Iterable<Mission> entities)
  {
    return this.repo.saveAll(entities);
  }
  
  public Optional<Mission> findById(Integer id)
  {
    return this.repo.findById(id);
  }
  
  public boolean existsById(Integer id)
  {
    return this.repo.existsById(id);
  }
  
  public Iterable<Mission> findAll()
  {
    return this.repo.findAll();
  }
  
  public Iterable<Mission> findAllById(Iterable<Integer> ids)
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
  
  public void delete(Mission entity)
  {
    this.repo.delete(entity);
  }
  
  public void deleteAll(Iterable<Mission> entities)
  {
    this.repo.deleteAll(entities);
  }
  
  public void deleteAll()
  {
    this.repo.deleteAll();
  }
}
