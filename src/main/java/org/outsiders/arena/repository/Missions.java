package org.outsiders.arena.repository;

import java.util.ArrayList;
import java.util.List;
import org.outsiders.arena.domain.Mission;
import org.springframework.stereotype.Service;

@Service
public class Missions {
	
	private List<Mission> missions = new ArrayList<>();
	
	public List<Mission> getAll(){
		return this.missions;
	}
	
	public Mission addMission(Mission c) {
		this.missions.add(c);
		return c;
	}
	
	public Mission getMission(int id) {
		return this.missions.get(id);
	}
}
