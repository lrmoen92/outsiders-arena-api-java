package org.outsiders.arena.domain;

import java.util.List;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;


@UserDefinedType
public class MissionProgress {
	
	private List<MissionRequirement> requirements;
	

	public List<MissionRequirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<MissionRequirement> requirements) {
		this.requirements = requirements;
	}
	
}
