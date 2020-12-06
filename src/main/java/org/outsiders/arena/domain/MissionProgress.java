package org.outsiders.arena.domain;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;


@UserDefinedType
public class MissionProgress {
	
	private int missionId;
	private List<MissionRequirement> requirements;
	

	public MissionProgress() {
		
	} 
	
	public MissionProgress(Mission input, List<MissionRequirement> progress) {
		this.missionId = input.getId();
		this.requirements = new ArrayList<>();
		for (MissionRequirement misReq : input.getRequirements()) {
			for (MissionRequirement prog : progress) {
				if (misReq.getTargetFaction().equals(prog.getTargetFaction()) && misReq.getUserFaction().equals(prog.getUserFaction())) {
					this.requirements.add(prog);
				}
			}
		}
	}

	public List<MissionRequirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<MissionRequirement> requirements) {
		this.requirements = requirements;
	}
	public int getMissionId() {
		return missionId;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	
}
