package org.outsiders.arena.domain;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.outsiders.arena.util.NRG;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
public class Battle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private boolean playerOneStart = new Random().nextBoolean();
	private String status;
	private String queue;
	private int turn = 0;
	private int arenaId;

	// Player here, - Players should have the teams and energy... That's a huge
	// refactor.

	private int playerIdOne;
	private int playerIdTwo;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Combatant> playerOneTeam;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Combatant> playerTwoTeam;

	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private Map<String, Integer> playerOneEnergy;

	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private Map<String, Integer> playerTwoEnergy;

	@JsonGetter
	public boolean getPlayerOneVictory() {
		for (Combatant c : this.playerTwoTeam) {
			if (c.getHp() > 0) {
				return false;
			}
		}
		return true;
	}

	@JsonGetter
	public boolean getPlayerTwoVictory() {
		for (Combatant c : this.playerOneTeam) {
			if (c.getHp() > 0) {
				return false;
			}
		}
		return true;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getArenaId() {
		return this.arenaId;
	}

	public void setArenaId(int arenaId) {
		this.arenaId = arenaId;
	}

	public boolean isPlayerOneStart() {
		return this.playerOneStart;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayerIdOne() {
		return this.playerIdOne;
	}

	public void setPlayerIdOne(int playerIdOne) {
		this.playerIdOne = playerIdOne;
	}

	public int getPlayerIdTwo() {
		return this.playerIdTwo;
	}

	public void setPlayerIdTwo(int playerIdTwo) {
		this.playerIdTwo = playerIdTwo;
	}

	public List<Combatant> getPlayerOneTeam() {
		return this.playerOneTeam;
	}

	public void setPlayerOneTeam(List<Combatant> playerOneTeam) {
		this.playerOneTeam = playerOneTeam;
	}

	public List<Combatant> getPlayerTwoTeam() {
		return this.playerTwoTeam;
	}

	public void setPlayerTwoTeam(List<Combatant> playerTwoTeam) {
		this.playerTwoTeam = playerTwoTeam;
	}

	public void setPlayerOneStart(boolean playerOneStart) {
		this.playerOneStart = playerOneStart;
	}

	public void drawPlayerTwoEnergy(int i) {
		NRG nrg = new NRG();
		this.setPlayerTwoEnergy(nrg.drawEnergy(i, this.getPlayerTwoEnergy()));
	}

	public void drawPlayerOneEnergy(int i) {
		NRG nrg = new NRG();
		this.setPlayerOneEnergy(nrg.drawEnergy(i, this.getPlayerOneEnergy()));
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	@Override
	public String toString() {
		return "Battle [playerOneStart=" + playerOneStart + ", turn=" + turn + ", arenaId=" + arenaId + ", playerIdOne="
				+ playerIdOne + ", playerIdTwo=" + playerIdTwo + ", "
				+ (playerOneEnergy != null ? "playerOneEnergy=" + playerOneEnergy + ", " : "")
				+ (playerTwoEnergy != null ? "playerTwoEnergy=" + playerTwoEnergy : "") + "]";
	}

	public Map<String, Integer> getPlayerOneEnergy() {
		return playerOneEnergy;
	}

	public void setPlayerOneEnergy(Map<String, Integer> playerOneEnergy) {
		this.playerOneEnergy = playerOneEnergy;
	}

	public Map<String, Integer> getPlayerTwoEnergy() {
		return playerTwoEnergy;
	}

	public void setPlayerTwoEnergy(Map<String, Integer> playerTwoEnergy) {
		this.playerTwoEnergy = playerTwoEnergy;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

}
