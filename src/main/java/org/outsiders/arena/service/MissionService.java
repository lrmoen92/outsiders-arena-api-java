package org.outsiders.arena.service;

import java.util.Optional;

import org.outsiders.arena.domain.Mission;

public abstract interface MissionService
{
  public abstract Mission save(Mission paramMission);
  
  public abstract Iterable<Mission> saveAll(Iterable<Mission> paramIterable);
  
  public abstract Optional<Mission> findById(Integer paramInteger);
  
  public abstract boolean existsById(Integer paramInteger);
  
  public abstract Iterable<Mission> findAll();
  
  public abstract Iterable<Mission> findAllById(Iterable<Integer> paramIterable);
  
  public abstract long count();
  
  public abstract void deleteById(Integer paramInteger);
  
  public abstract void delete(Mission paramMission);
  
  public abstract void deleteAll(Iterable<Mission> paramIterable);
  
  public abstract void deleteAll();
}
