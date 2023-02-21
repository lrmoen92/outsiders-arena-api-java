package org.outsiders.arena.service;

import java.util.List;
import java.util.Optional;

import org.outsiders.arena.domain.Mission;

public abstract interface MissionService {
	
  public abstract Mission save(Mission paramMission);
  
  public abstract Mission findById(Integer paramInteger);
  
  public abstract List<Mission> findAll();
}
