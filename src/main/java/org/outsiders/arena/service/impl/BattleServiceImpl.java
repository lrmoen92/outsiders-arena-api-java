package org.outsiders.arena.service.impl;

import java.util.Optional;

import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.Player;
import org.outsiders.arena.repository.BattleRepository;
import org.outsiders.arena.service.BattleService;
import org.outsiders.arena.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleServiceImpl
  implements BattleService
{
  @Autowired
  private BattleRepository repo;
  
  @Autowired
  private PlayerService playerService;
  
  public Battle getByPlayerId(int id)
  {
    Iterable<Battle> battles = this.repo.findAll();
    for (Battle b : battles) {
      if (b.getPlayerIdOne() == id || b.getPlayerIdTwo() == id) {
        return b;
      }
    }
    return null;
  }
  
  public Battle getByPlayerDisplayName(String name)
  {
	  Player player = playerService.findByDisplayName(name);
	  if (player != null) {
		  for (Battle b : repo.findAll()) {
			  if (b.getPlayerIdOne() == player.getId()) {
				  return b;
			  }
		  }
		  return null;
	  } else {
		  return null;
	  }
  }
  
  public Battle getByArenaId(int id)
  {
    Iterable<Battle> battles = this.repo.findAll();
    for (Battle b : battles) {
      if (b.getArenaId() == id) {
        return b;
      }
    }
    return null;
  }
  
  public Battle save(Battle entity)
  {
    return (Battle)this.repo.save(entity);
  }
  
  public Iterable<Battle> saveAll(Iterable<Battle> entities)
  {
    return this.repo.saveAll(entities);
  }
  
  public Optional<Battle> findById(Integer id)
  {
    return this.repo.findById(id);
  }
  
  public boolean existsById(Integer id)
  {
    return this.repo.existsById(id);
  }
  
  public Iterable<Battle> findAll()
  {
    return this.repo.findAll();
  }
  
  public Iterable<Battle> findAllById(Iterable<Integer> ids)
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
  
  public void delete(Battle entity)
  {
    this.repo.delete(entity);
  }
  
  public void deleteAll(Iterable<Battle> entities)
  {
    this.repo.deleteAll(entities);
  }
  
  public void deleteAll()
  {
    this.repo.deleteAll();
  }
}
