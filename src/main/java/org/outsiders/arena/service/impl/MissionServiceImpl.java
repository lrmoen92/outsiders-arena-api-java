package org.outsiders.arena.service.impl;

import java.util.List;
import org.outsiders.arena.domain.Mission;
import org.outsiders.arena.repository.Missions;
import org.outsiders.arena.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MissionServiceImpl implements MissionService {
	
  @Autowired
  private Missions missions;
  
  public Mission save(Mission entity)
  {
    return this.missions.addMission(entity);
  }
  
  public Mission findById(Integer id)
  {
    return this.missions.getMission(id);
  }

  public List<Mission> findAll()
  {
    return this.missions.getAll();
  }

}
