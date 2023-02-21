package org.outsiders.arena.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Ability {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int cooldown = 0;
	private String name;
	private String abilityUrl;
	private String description;
	private int position;
	private String targets;
	private String types;

	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<String> cost = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<BattleEffect> selfEffects = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<BattleEffect> enemyEffects = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<BattleEffect> aoeEnemyEffects = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<BattleEffect> allyEffects = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<BattleEffect> aoeAllyEffects = new ArrayList<>();

	private boolean aoe = false;
	private boolean self = false;
	private boolean ally = false;
	private boolean enemy = false;

	public Ability() {
	}

	public Ability(boolean enemies, boolean allies, boolean self, boolean aoe) {
		this.enemy = enemies;
		this.ally = allies;
		this.self = self;
		this.aoe = aoe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTargets() {
		return this.targets;
	}

	public String getTypes() {
		return this.types;
	}

	@JsonIgnore
	public String getCleanCost() {
		StringBuilder sb = new StringBuilder();
		int div = 0;
		int arc = 0;
		int dex = 0;
		int str = 0;
		int ran = 0;
		for (String cost : this.cost) {
			if (cost.equals(Energy.DIVINITY)) {
				div++;
			} else if (cost.equals(Energy.ARCANA)) {
				arc++;
			} else if (cost.equals(Energy.DEXTERITY)) {
				dex++;
			} else if (cost.equals(Energy.STRENGTH)) {
				str++;
			} else if (cost.equals(Energy.RANDOM)) {
				ran++;
			}
		}
		;

		if (div > 0) {
			sb.append(div);
			sb.append(" Divinity");
		}
		if (arc > 0) {
			sb.append(arc);
			sb.append(" Arcana");
		}
		if (dex > 0) {
			sb.append(dex);
			sb.append(" Dexterity");
		}
		if (str > 0) {
			sb.append(str);
			sb.append(" Strength");
		}
		if (ran > 0) {
			sb.append(ran);
			sb.append(" Random");
		}
		return sb.toString();
	}

	@JsonIgnore
	public String getShorthand() {
		return "CD: " + this.cooldown + " - " + this.name + " - " + this.getCleanCost() + ": " + this.description;
	}

	@JsonIgnore
	public void setTargets() {
		List<String> list = new ArrayList<>();
		if (this.isSelf()) {
			list.add("Self");
		}

		if (this.isAoe()) {
			if (this.isAlly()) {
				list.add("Allies");
			}
			if (this.isEnemy()) {
				list.add("Enemies");
			}
		} else {
			if (this.isAlly()) {
				list.add("Ally");
			} else if (this.isEnemy()) {
				list.add("Enemy");
			}
		}
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			sb.append(s);
			if (i != list.size() - 1) {
				sb.append(" and ");
			}
		}

		this.targets = sb.toString();
	}

	@JsonIgnore
	public void setTypes() {
		List<String> list = new ArrayList<>();
		if (this.isPhysical()) {
			list.add("Physical");
		}
		if (this.isMagical()) {
			list.add("Magical");
		}
		if (this.isAffliction()) {
			list.add("Affliction");
		}
		if (this.isDamaging()) {
			list.add("Damaging");
		}
		if (this.isDebuff()) {
			list.add("Debuff");
		}
		if (this.isBuff()) {
			list.add("Buff");
		}
		if (this.isInterruptable()) {
			list.add("Interruptable");
		}
		if (!this.isVisible()) {
			list.add("Hidden");
		}
		if (this.isStacks()) {
			list.add("Stacks");
		}
		// TODO LATER: Interruptable, Conditional, Visible, Stacks

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			sb.append(s);
			if (i != list.size() - 1) {
				sb.append(", ");
			}
		}

		this.types = sb.toString();
	}

	@JsonIgnore
	public Map<String, String> getRundown() {
		Map<String, String> map = new HashMap<>();

		map.put("TARGETS", this.getTargets());
		map.put("TYPES", this.getTypes());

		return map;
	}

	@JsonIgnore
	public boolean isInterruptable() {
		for (BattleEffect e : this.selfEffects) {
			if (e.isInterruptable()) {
				return true;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.isInterruptable()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.isInterruptable()) {
				return true;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.isInterruptable()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.isInterruptable()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isVisible() {
		for (BattleEffect e : this.selfEffects) {
			if (!e.isVisible()) {
				return false;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (!e.isVisible()) {
				return false;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (!e.isVisible()) {
				return false;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (!e.isVisible()) {
				return false;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (!e.isVisible()) {
				return false;
			}
		}
		return true;
	}

	@JsonIgnore
	public boolean isStacks() {
		for (BattleEffect e : this.selfEffects) {
			if (e.isStacks()) {
				return true;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.isStacks()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.isStacks()) {
				return true;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.isStacks()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.isStacks()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isPhysical() {
		for (BattleEffect e : this.selfEffects) {
			if (e.isPhysical()) {
				return true;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.isPhysical()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.isPhysical()) {
				return true;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.isPhysical()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.isPhysical()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isMagical() {
		for (BattleEffect e : this.selfEffects) {
			if (e.isMagical()) {
				return true;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.isMagical()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.isMagical()) {
				return true;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.isMagical()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.isMagical()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isAffliction() {
		for (BattleEffect e : this.selfEffects) {
			if (e.isAffliction()) {
				return true;
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.isAffliction()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.isAffliction()) {
				return true;
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.isAffliction()) {
				return true;
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.isAffliction()) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isDamaging() {
		for (BattleEffect e : this.selfEffects) {
			if (e.getStatMods().get(Stat.DAMAGE) != null) {
				if (e.getStatMods().get(Stat.DAMAGE) > 0) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.getStatMods().get(Stat.DAMAGE) != null) {
				if (e.getStatMods().get(Stat.DAMAGE) > 0) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.getStatMods().get(Stat.DAMAGE) != null) {
				if (e.getStatMods().get(Stat.DAMAGE) > 0) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.getStatMods().get(Stat.DAMAGE) != null) {
				if (e.getStatMods().get(Stat.DAMAGE) > 0) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.getStatMods().get(Stat.DAMAGE) != null) {
				if (e.getStatMods().get(Stat.DAMAGE) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isBuff() {
		for (BattleEffect e : this.selfEffects) {
			if (e.getStatMods() != null) {
				for (String buff : Stat.BUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) > 0) {
							return true;
						}
					}
				}
				for (String buff : Stat.DEBUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) < 0) {
							return true;
						}
					}
				}
			}
			for (String buff : Quality.BUFFS) {
				if (buff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.allyEffects) {
			if (e.getStatMods() != null) {
				for (String buff : Stat.BUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) > 0) {
							return true;
						}
					}
				}
				for (String buff : Stat.DEBUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) < 0) {
							return true;
						}
					}
				}
			}
			for (String buff : Quality.BUFFS) {
				if (buff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.aoeAllyEffects) {
			if (e.getStatMods() != null) {
				for (String buff : Stat.BUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) > 0) {
							return true;
						}
					}
				}
				for (String buff : Stat.DEBUFFS) {
					if (e.getStatMods().get(buff) != null) {
						if (e.getStatMods().get(buff) < 0) {
							return true;
						}
					}
				}
			}
			for (String buff : Quality.BUFFS) {
				if (buff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isDebuff() {
		for (BattleEffect e : this.selfEffects) {
			if (e.getStatMods() != null) {
				for (String debuff : Stat.BUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) < 0) {
							return true;
						}
					}
				}
				for (String debuff : Stat.DEBUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) > 0) {
							return true;
						}
					}
				}
			}
			for (String debuff : Quality.DEBUFFS) {
				if (debuff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.enemyEffects) {
			if (e.getStatMods() != null) {
				for (String debuff : Stat.BUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) < 0) {
							return true;
						}
					}
				}
				for (String debuff : Stat.DEBUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) > 0) {
							return true;
						}
					}
				}
			}
			for (String debuff : Quality.DEBUFFS) {
				if (debuff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		for (BattleEffect e : this.aoeEnemyEffects) {
			if (e.getStatMods() != null) {
				for (String debuff : Stat.BUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) < 0) {
							return true;
						}
					}
				}
				for (String debuff : Stat.DEBUFFS) {
					if (e.getStatMods().get(debuff) != null) {
						if (e.getStatMods().get(debuff) > 0) {
							return true;
						}
					}
				}
			}
			for (String debuff : Quality.DEBUFFS) {
				if (debuff.equals(e.getQuality())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public List<String> getCost() {
		return this.cost;
	}

	public void setCost(List<String> cost) {
		this.cost = cost;
	}

	public List<BattleEffect> getAllyEffects() {
		return this.allyEffects;
	}

	public void setAllyEffects(List<BattleEffect> allyEffects) {
		this.allyEffects = allyEffects;
	}

	public boolean isAlly() {
		return this.ally;
	}

	public void setAlly(boolean ally) {
		this.ally = ally;
	}

	public boolean isEnemy() {
		return this.enemy;
	}

	public void setEnemy(boolean enemy) {
		this.enemy = enemy;
	}

	public List<BattleEffect> getSelfEffects() {
		return this.selfEffects;
	}

	public void setSelfEffects(List<BattleEffect> selfEffects) {
		this.selfEffects = selfEffects;
	}

	public List<BattleEffect> getAoeEnemyEffects() {
		return this.aoeEnemyEffects;
	}

	public void setAoeEnemyEffects(List<BattleEffect> aoeEnemyEffects) {
		this.aoeEnemyEffects = aoeEnemyEffects;
	}

	public void setAoe(boolean aoe) {
		this.aoe = aoe;
	}

	public void setSelf(boolean self) {
		this.self = self;
	}

	public boolean isAoe() {
		return this.aoe;
	}

	public boolean isSelf() {
		return this.self;
	}

	public String getAbilityUrl() {
		return abilityUrl;
	}

	public void setAbilityUrl(String abilityUrl) {
		this.abilityUrl = abilityUrl;
	}

	public List<BattleEffect> getEnemyEffects() {
		return enemyEffects;
	}

	public void setEnemyEffects(List<BattleEffect> enemyEffects) {
		this.enemyEffects = enemyEffects;
	}

	public List<BattleEffect> getAoeAllyEffects() {
		return aoeAllyEffects;
	}

	public void setAoeAllyEffects(List<BattleEffect> aoeAllyEffects) {
		this.aoeAllyEffects = aoeAllyEffects;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public void setTypes(String types) {
		this.types = types;
	}
}
