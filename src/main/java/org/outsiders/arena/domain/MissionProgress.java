package org.outsiders.arena.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name = "MissionProgress", schema = "outsiders")
@Embeddable
public class MissionProgress
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
	private int missionId;
	  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	  @Fetch(value = FetchMode.SUBSELECT)
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
