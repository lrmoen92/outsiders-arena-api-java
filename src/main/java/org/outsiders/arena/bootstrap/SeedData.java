package org.outsiders.arena.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleEffect;
import org.outsiders.arena.domain.Combatant;
import org.outsiders.arena.domain.Conditional;
import org.outsiders.arena.domain.Cost;
import org.outsiders.arena.domain.Faction;
import org.outsiders.arena.domain.Mission;
import org.outsiders.arena.domain.MissionProgress;
import org.outsiders.arena.domain.MissionRequirement;
import org.outsiders.arena.domain.Player;
import org.outsiders.arena.domain.PlayerCredentials;
import org.outsiders.arena.domain.Quality;
import org.outsiders.arena.domain.Stat;
import org.outsiders.arena.service.BattleService;
import org.outsiders.arena.service.CharacterService;
import org.outsiders.arena.service.MissionService;
import org.outsiders.arena.service.PlayerService;
import org.outsiders.arena.util.NRG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class SeedData implements CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(SeedData.class);
	@Autowired
	private BattleService battleService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private CharacterService characterService;
	@Autowired
	private MissionService missionService;

	public void run(String... args) {
		try {
			LOG.info("SEED DATA RUN");

			makeOutsiders();
			makeMissions();
			playerService.deleteAll();
			makeUsers();
			battleService.deleteAll();
//			makeBattle();
//			makeBattle();
		} catch (Exception e) {
			LOG.info("SEED EXCEPTION");

		}
		LOG.info("ALL DATA SEEDED SUCCESSFULLY");
	}

	private Map<String, Integer> buildStat(String a, Integer b) {
		Map<String, Integer> map = new HashMap();
		map.put(a, b);
		return map;
	}

	private List<BattleEffect> buildEffects(List<BattleEffect> l, int duration, String name, String description,
			String url, boolean physical, boolean magical, boolean affliction, boolean interruptable,
			boolean conditional, boolean visible, boolean stacks, Map<String, Integer> statMods, String quality,
			String condition) {
		BattleEffect e = new BattleEffect(physical, magical, affliction, interruptable, conditional);
		e.setName(name);
		e.setDescription(description);
		e.setAvatarUrl(url);
		e.setDuration(duration);
		e.setCondition(condition);
		if (statMods != null) {
			e.setStatMods(statMods);
		}
		e.setQuality(quality);
		e.setVisible(visible);
		e.setStacks(stacks);

		if (l == null) {
			l = new ArrayList<>();
		}
		l.add(e);
		return l;
	}

	private List<Ability> buildAbilities(List<Ability> l, boolean enemies, boolean allies, boolean self, boolean aoe,
			List<String> cost, int cooldown, String name, String url, String description, List<BattleEffect> effects,
			List<BattleEffect> selfEffects, List<BattleEffect> allyEffects, List<BattleEffect> aoeEnemyEffects,
			List<BattleEffect> aoeAllyEffects) {
		Ability a = new Ability(enemies, allies, self, aoe);
		a.setCooldown(cooldown);
		a.setName(name);
		a.setAbilityUrl(url);
		a.setCost(cost);
		if (effects != null) {
			a.setEnemyEffects(effects);
		}

		if (selfEffects != null) {
			a.setSelfEffects(selfEffects);
		}

		if (allyEffects != null) {
			a.setAllyEffects(allyEffects);
		}

		if (aoeEnemyEffects != null) {
			a.setAoeEnemyEffects(aoeEnemyEffects);
		}

		if (aoeAllyEffects != null) {
			a.setAoeAllyEffects(aoeAllyEffects);
		}
		a.setDescription(description);
		a.setTargets();
		a.setTypes();

		if (l == null) {
			l = new ArrayList<>();
		}
		a.setPosition(l.size());
		l.add(a);
		return l;
	}

	private int red;
	private int blue;

	private String makeBattle() {
		int arenaId = 1;
		String queue = "QUICK";
		Battle battle = this.battleService.getByArenaId(arenaId);
		if (battle == null) {
			Integer playerId = red;
			Integer characterId1 = 1;
			Integer characterId2 = 2;
			Integer characterId3 = 3;
			battle = new Battle();
			battle.setId(NRG.randomInt());
			battle.setStatus("QUEUEING");
			battle.setQueue(queue);
			battle.setArenaId(arenaId);
			battle.setPlayerIdOne(playerId.intValue());
			ArrayList<Combatant> list1 = new ArrayList<Combatant>();
			Combatant i1 = new Combatant(characterService.findById(characterId1.intValue()));
			Combatant i2 = new Combatant(characterService.findById(characterId2.intValue()));
			Combatant i3 = new Combatant(characterService.findById(characterId3.intValue()));
			i1.setPosition(0);
			i2.setPosition(1);
			i3.setPosition(2);
			list1.add(i1);
			list1.add(i2);
			list1.add(i3);
			battle.setPlayerOneTeam(list1);

			Battle savedBattle = this.battleService.save(battle);
			LOG.info("SAVED BATTLE:: " + savedBattle.toString());
			return new Gson().toJson("WAITING FOR OPPONENTS");
		} else {
			Integer playerId = blue;
			Integer characterId1 = 4;
			Integer characterId2 = 5;
			Integer characterId3 = 6;
			battle.setStatus("MATCHING");
			ArrayList<Combatant> list1 = new ArrayList<Combatant>();
			Combatant i1 = new Combatant(characterService.findById(characterId1.intValue()));
			Combatant i2 = new Combatant(characterService.findById(characterId2.intValue()));
			Combatant i3 = new Combatant(characterService.findById(characterId3.intValue()));
			i1.setPosition(3);
			i2.setPosition(4);
			i3.setPosition(5);
			list1.add(i1);
			list1.add(i2);
			list1.add(i3);
			battle.setPlayerTwoTeam(list1);
			battle.setPlayerIdTwo(playerId.intValue());

			if (battle.isPlayerOneStart()) {
				battle.drawPlayerOneEnergy(1);
				battle.drawPlayerTwoEnergy(3);
			} else {
				battle.drawPlayerOneEnergy(3);
				battle.drawPlayerTwoEnergy(1);
			}

			Battle savedBattle = this.battleService.save(battle);

			battle.setStatus("STARTING");

			LOG.info("SAVED BATTLE:: " + savedBattle.toString());

			Player playerOne = this.playerService.findById(Integer.valueOf(battle.getPlayerIdOne())).get();
			Player playerTwo = this.playerService.findById(Integer.valueOf(battle.getPlayerIdTwo())).get();
			LOG.info("Match Made between {} and {}", playerOne.getDisplayName(), playerTwo.getDisplayName());
			String battleJson = new Gson().toJson(savedBattle);
			String playerOneJson = new Gson().toJson(playerOne);
			String playerTwoJson = new Gson().toJson(playerTwo);

			if (battleJson != null && playerOne != null && playerTwo != null) {
				String responseJson = "{\"type\": \"INIT\", \"battle\": " + battleJson + ",\"playerOne\": "
						+ playerOneJson + ",\"playerTwo\": " + playerTwoJson + "}";
				return responseJson;
			} else {
				return new Gson().toJson("ERROR");
			}
		}
	}

	private int makeRed() {
		Player player = new Player();
		PlayerCredentials creds = new PlayerCredentials();
		creds.setEmail("Red@Red.com");
		creds.setPassword("red");
		int randomNum = -19024288;
		player.setId(randomNum);
		player.setLevel(1);
		player.setDisplayName("Red");
		player.setAvatarUrl("https://i.imgur.com/x8VwSea.png");
		player.setCredentials(creds);

		Set<Integer> chars = new HashSet<>();
		chars.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
		player.setCharacterIdsUnlocked(chars);

		Set<Integer> miss = new HashSet<>();
		miss.add(0);
		player.setMissionIdsCompleted(miss);

		List<MissionProgress> prog = new ArrayList<>();
		prog.add(new MissionProgress());
		player.setMissionProgress(prog);

		player = this.playerService.save(player);
		LOG.info("Created new Player: " + player.toString());

		return player.getId();
	}

	private int makeBlue() {
		Player player = new Player();
		PlayerCredentials creds = new PlayerCredentials();
		creds.setEmail("Blue@Blue.com");
		creds.setPassword("blue");
		int randomNum = -19024287;
		player.setId(randomNum);
		player.setLevel(1);
		player.setDisplayName("Blue");
		player.setAvatarUrl("https://i.imgur.com/d8MKvv0.png");
		player.setCredentials(creds);

		Set<Integer> chars = new HashSet<>();
		chars.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
		player.setCharacterIdsUnlocked(chars);

		Set<Integer> miss = new HashSet<>();
		miss.add(0);
		player.setMissionIdsCompleted(miss);

		List<MissionProgress> prog = new ArrayList<>();
		prog.add(new MissionProgress());
		player.setMissionProgress(prog);

		player = this.playerService.save(player);
		LOG.info("Created new Player: " + player.toString());

		return player.getId();
	}

	private void makeUsers() {
		int red = makeRed();
		int blue = makeBlue();
		this.red = red;
		this.blue = blue;
		LOG.info("Saved new User: " + red);
		LOG.info("Saved new User: " + blue);
	}

	private void makeOutsiders() {
		LOG.info("Saved new Character: " + makeAlex().toString());
		LOG.info("Saved new Character: " + makeFainne().toString());
		LOG.info("Saved new Character: " + makeShinzo().toString());
		LOG.info("Saved new Character: " + makeHollyanna().toString());
		LOG.info("Saved new Character: " + makeGeddy().toString());
		LOG.info("Saved new Character: " + makeTristane().toString());
		LOG.info("Saved new Character: " + makeDrundar().toString());
	}

	private void makeMissions() {
		LOG.info("Saved new Mission: " + makeTristaneMission().toString());
		LOG.info("Saved new Mission: " + makeDrundarMission().toString());
	}

	private Mission makeDrundarMission() {
		Mission m = new Mission();

		m.setId(2);
		m.setAvatarUrl("/assets/drundar.png");
		m.setCharacterIdUnlocked(7);
		m.setDescription("Drundar is tearing down the place, no choice but to fight!");
		m.setMinmumLevel(6);
		m.setName("Walls Come Tumbling Down");

		List<MissionRequirement> requirements = new ArrayList<>();

		MissionRequirement mq1 = new MissionRequirement();
		mq1.setAmount(3);
		mq1.setUserFaction(Faction.TRISTANE);

		MissionRequirement mq2 = new MissionRequirement();
		mq2.setAmount(3);
		mq2.setUserFaction(Faction.GEDDY);

		MissionRequirement mq3 = new MissionRequirement();
		mq3.setAmount(3);
		mq3.setUserFaction(Faction.ALEX);

		MissionRequirement mq4 = new MissionRequirement();
		mq4.setAmount(3);
		mq4.setUserFaction(Faction.SHINZO);

		requirements.add(mq1);
		requirements.add(mq2);
		requirements.add(mq3);
		requirements.add(mq4);

		m.setRequirements(requirements);

		return this.missionService.save(m);
	}

	private Mission makeTristaneMission() {
		Mission m = new Mission();

		m.setId(1);
		m.setAvatarUrl("/assets/tristane.png");
		m.setCharacterIdUnlocked(6);
		m.setDescription("Gotta complete a job for Tristane.");
		m.setMinmumLevel(0);
		m.setName("Guild Duties");

		List<MissionRequirement> requirements = new ArrayList<>();

		MissionRequirement mq1 = new MissionRequirement();
		mq1.setAmount(3);
		mq1.setUserFaction(Faction.ALEX);

		MissionRequirement mq2 = new MissionRequirement();
		mq2.setAmount(3);
		mq2.setUserFaction(Faction.GEDDY);

		requirements.add(mq1);
		requirements.add(mq2);
		m.setRequirements(requirements);

		return this.missionService.save(m);
	}

	private Combatant makeAlex() {
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 3, "Impulse", "This unit is taking 10 magic damage",
				"/assets/alex1.png", false, true, false, true, false, true, false, buildStat(Stat.DAMAGE, 10), null,
				null);

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, false, Cost.oneRan, 4, "Impulse",
				"/assets/alex1.png",
				"Alex uses Impulse to attack one enemy, dealing 10 points of magic damage for 3 turns.", effects, null,
				null, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 1, "Flash Freeze", "This unit took damage from Flash Freeze",
				"/assets/alex2.png", false, false, true, false, false, true, false, buildStat(Stat.DAMAGE, 20), null,
				null);

		effects1 = buildEffects(effects1, 2, "Flash Frozen", "This unit will do 10 less damage", "/assets/alex2.png",
				false, false, true, false, false, true, false, buildStat(Stat.DAMAGE_OUT, -10), null, null);

		// ABILITY 2
		test = buildAbilities(test, true, false, false, false, Cost.oneDex, 2, "Flash Freeze", "/assets/alex2.png",
				"Alex freezes one enemy with Flash Freeze, dealing 20 affliction damage, and reducing their damage by 10 for 2 turns",
				effects1, null, null, null, null);

		// EFFECTS 3

		List<BattleEffect> effects2 = buildEffects(null, 2, "Study Target",
				"This unit takes 10 more damage from all sources", "/assets/alex3.png", true, false, false, false,
				false, true, false, buildStat(Stat.DAMAGE_IN, 10), null, null);

		effects2 = buildEffects(effects2, 1, "Study Target", "This unit's traps have been revealed",
				"/assets/alex3.png", true, false, false, false, false, true, false, null, Quality.REVEALED, null);

		// ABILITY 3
		test = buildAbilities(test, true, false, false, false, Cost.twoRan, 3, "Study Target", "/assets/alex3.png",
				"Alex reveals and disables any hidden skills on the target, and increases the damage target takes from all sources by 10 for 2 turns.",
				effects2, null, null, null, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Stealth", "Alex is invunerable", "/assets/alex4.png", true,
				false, false, false, false, true, false, null, Quality.INVULNERABLE, null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Stealth", "/assets/alex4.png",
				"Alex hides for a turn, going invulnerable", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(0);
		c.setCharacterId(0);
		c.setName("Alex Drake");
		c.setAvatarUrl("/assets/alex.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.ALEX);
		facts.add(Faction.OUTSIDERS);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

	// buildEffects() existing effects, duration,
	// name, description,
	// physical, magical, interuptable, conditional, visible
	// Stat changes, Quality changes, Conditional requisites

	// buildAbilities() existing abilities,
	// enemies, allies, self, aoe,
	// Cost.something, cooldown,
	// name, url,
	// description,
	// effects, self, ally, aoeEnemy, aoeAlly

	private Combatant makeFainne() {
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 2, "Unyielding Spirit",
				"This unit will gain 1 Random Energy if attacked", "/assets/fainne1.png", false, true, false, true,
				true, false, false, buildStat(Stat.ENERGY_CHANGE, 1), null,
				Conditional.TARGET_WAS(Conditional.ATTACKED));

		// ABILITY 1
		List<Ability> test = buildAbilities(null, false, true, true, false, Cost.oneDiv, 3, "Unyielding Spirit",
				"/assets/fainne1.png",
				"Fainne's unyielding spirit supplies 1 random energy to an ally or herself if they are attacked, for 2 turns",
				null, null, effects, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 4, "Call Lightning", "This unit is taking 5 damage",
				"/assets/fainne2.png", false, true, false, true, false, true, false, buildStat(Stat.DAMAGE, 5),
				Quality.AFFECTED_BY("Call Lightning"), null);

		// ABILITY 2
		test = buildAbilities(test, true, false, false, false, Cost.oneRan, 1, "Call Lightning", "/assets/fainne2.png",
				"Fainne summons bolts of lighting from above, dealing 5 damage to one enemy for four turns.", effects1,
				null, null, null, null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 1, "Zahra", "This unit took extra damage from Zahra",
				"/assets/fainne3.png", true, false, false, false, true, true, false, buildStat(Stat.BONUS_DAMAGE, 10),
				null, Conditional.TARGET_AFFECTED_BY("Call Lightning"));

		effects2 = buildEffects(effects2, 1, "Zahra", "This unit took damage from Zahra", "/assets/fainne3.png", true,
				false, false, false, false, true, false, buildStat(Stat.DAMAGE, 20), null, null);

		// ABILITY 3
		test = buildAbilities(test, true, false, false, false, Cost.oneStr, 1, "Zahra", "/assets/fainne3.png",
				"Fainee lets loose Zahra to maim one enemy for 20 dmg.  10 extra damage if target is under the effects of Call Lightning",
				effects2, null, null, null, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Irethis",
				"Fainne calls for Irethis' aid, going invulnerable", "/assets/fainne4.png", true, false, false, false,
				false, true, false, null, Quality.INVULNERABLE, null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Irethis", "/assets/fainne4.png",
				"Fainne calls for Irethis' aid, going invulnerable", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(1);
		c.setCharacterId(1);
		c.setName("Fainne");
		c.setAvatarUrl("/assets/fainne.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.FAINNE);
		facts.add(Faction.OUTSIDERS);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

	private Combatant makeShinzo() {
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 1, "Trident",
				"Shinzo has dealt physical damage with his trident", "/assets/shinzo1.png", true, false, false, false,
				false, true, false, buildStat(Stat.DAMAGE, 25), null, null);

		effects = buildEffects(effects, 1, "Trident", "Shinzo has dealt magical damage with his trident",
				"/assets/shinzo1.png", false, true, false, false, false, true, false, buildStat(Stat.DAMAGE, 15), null,
				null);

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, false, Cost.oneStrOneArc, 2, "Trident",
				"/assets/shinzo1.png",
				"Shinzo brings the storm with his trident, doing 25 physical damage and 15 magical damage", effects,
				null, null, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 1, "Entangle", "This unit is taking 15 damage",
				"/assets/shinzo2.png", false, true, false, false, false, true, false, buildStat(Stat.DAMAGE, 15), null,
				null);

		effects1 = buildEffects(effects1, 1, "Entangle", "This unit is stunned", "/assets/shinzo2.png", false, true,
				false, false, false, true, false, null, Quality.STUNNED, null);

		// ABILITY 2
		test = buildAbilities(test, true, false, false, false, Cost.oneDiv, 4, "Entangle", "/assets/shinzo2.png",
				"Shinzo entangles an enemy doing 15 damage, and stunning them", effects1, null, null, null, null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 1, "Channel Energy", "This unit is healing 20 HP",
				"/assets/shinzo3.png", false, true, false, false, false, true, false, buildStat(Stat.DAMAGE, -20), null,
				null);

		// ABILITY 3
		test = buildAbilities(test, false, true, true, true, Cost.oneDivOneRan, 4, "Channel Energy",
				"/assets/shinzo3.png", "Shinzo calls upon his divine power and heals his party for 20 hp", null, null,
				null, null, effects2);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Captain's Orders",
				"Shinzo's the Captain.  He's invulnerable this turn.", "/assets/shinzo4.png", true, false, false, false,
				false, true, false, null, Quality.INVULNERABLE, null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Captain's Orders",
				"/assets/shinzo4.png", "Shinzo call's upon his status among the seas to become invulnerable this turn.",
				null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(2);
		c.setCharacterId(2);
		c.setName("Shinzo Katetsu");
		c.setAvatarUrl("/assets/shinzo.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.SHINZO);
		facts.add(Faction.OUTSIDERS);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

	private Combatant makeHollyanna() {
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 1, "Sneak Attack", "Sneak Attack Damage", "/assets/holly1.png",
				true, false, false, false, false, true, false, buildStat(Stat.DAMAGE, 25), null, null);

//	  effects = buildEffects(effects, 1, 
//			  "Sneak Attack", "Sneak Attack Bonus Damage",  "/assets/holly1.png",
//			  true, false, false, false, true, true, false,
//			  buildStat(Stat.BONUS_DAMAGE, 10), null, Conditional.TARGET_HAS_QUALITY(Quality.STUNNED));

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, false, Cost.oneRan, 2, "Sneak Attack",
				"/assets/holly1.png", "Hollyanna hides in the shadows, and then strikes, dealing 25 damage.", effects,
				null, null, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 1, "Eliminate",
				"Hollyanna uses raw force to inflict a mortal wound.", "/assets/holly2.png", true, false, false, false,
				false, true, false, buildStat(Stat.DAMAGE, 40), null, null);

		List<BattleEffect> selfEffects1 = buildEffects(null, 3, "Eliminate - Bloodbath",
				"Hollyanna is being baptised in blood, Fulfill Contract is empowered until the end of next turn.",
				"/assets/holly2.png", true, false, false, false, true, true, false, null,
				Quality.AFFECTED_BY("Bloodbath"), Conditional.USER_DID(Conditional.KILL));

		// ABILITY 2
		test = buildAbilities(test, true, false, true, false, Cost.oneStrOneDex, 2, "Eliminate", "/assets/holly2.png",
				"Hollyanna uses raw force to inflict a mortal wound, dealing 40 damage.  If this skill kills an enemy, Fulfill Contract is empowered for 2 turns.",
				effects1, selfEffects1, null, null, null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 1, "Fulfill Contract",
				"Take part in an evil deed, stunning all enemies for one turn.", "/assets/holly3.png", false, true,
				false, false, false, true, false, null, Quality.STUNNED, null);

		List<BattleEffect> selfEffects2 = buildEffects(null, 1, "Fulfill Contract", "Hollyanna is untargetable.",
				"/assets/holly3.png", false, true, false, false, true, true, false, buildStat(Stat.DAMAGE, -40), null,
				Conditional.USER_AFFECTED_BY("Bloodbath"));

		selfEffects2 = buildEffects(selfEffects2, 1, "Fulfill Contract", "Hollyanna is untargetable.",
				"/assets/holly3.png", false, true, false, false, true, true, false, null, Quality.UNTARGETABLE,
				Conditional.USER_AFFECTED_BY("Bloodbath"));

		selfEffects2 = buildEffects(selfEffects2, -1, "Fulfill Contract",
				"If this skill is used 3 times, Hollyanna dies.", "/assets/holly3.png", false, true, false, false,
				false, true, true, null, Quality.AFFECTED_BY("Fulfill Contract"), null);

		selfEffects2 = buildEffects(selfEffects2, 0, "Fulfill Contract",
				"If this skill is used 3 times, Hollyanna dies.", "/assets/holly3.png", false, true, false, false, true,
				true, false, buildStat(Stat.TRUE_DAMAGE, 100), null,
				Conditional.USER_AFFECTED_BY("Fulfill Contract", 3));

		// ABILITY 3
		test = buildAbilities(test, true, false, true, true, Cost.twoDiv, 2, "Fulfill Contract", "/assets/holly3.png",
				"Take part in an evil deed, stunning all enemies for one turn.  If Bloodbath is active, heal 40 hp, and become untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
				null, selfEffects2, null, effects2, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Shadow Illusion", "This unit is invulnerable.",
				"/assets/holly4.png", true, false, false, false, false, true, false, null, Quality.INVULNERABLE, null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Shadow Illusion", "/assets/holly4.png",
				"Hollyanna hides in shadow, going invulnerable for 1 turn", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(3);
		c.setCharacterId(3);
		c.setName("Hollyanna Knox");
		c.setAvatarUrl("/assets/holly.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.HOLLYANNA);
		facts.add(Faction.OUTSIDERS);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

	private Combatant makeGeddy() {
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 1, "Darts", "This unit took damage from darts",
				"/assets/geddy1.png", true, false, false, false, false, true, false, buildStat(Stat.DAMAGE, 15), null,
				null);

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, true, Cost.oneDex, 1, "Darts",
				"/assets/geddy1.png", "Geddy tosses darts at all enemies, dealing 15 damage", null, null, null, effects,
				null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 2, "Inspiration", "This unit will gain 10 shields.",
				"/assets/geddy2.png", false, true, false, true, false, true, false, buildStat(Stat.SHIELD_GAIN, 10),
				null, null);

		// ABILITY 2
		test = buildAbilities(test, false, true, false, false, Cost.free, 4, "Inspiration", "/assets/geddy2.png",
				"Geddy inspires an ally for 2 turns, giving them 10 shields each turn.", null, null, effects1, null,
				null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 2, "Geas",
				"This unit is following their Geas, and cannot become invulnerable.", "/assets/geddy3.png", false, true,
				false, false, false, true, false, null, Quality.VULNERABLE, null);

		effects2 = buildEffects(effects2, 2, "Geas", "This unit is following their Geas, and is stunned.",
				"/assets/geddy3.png", false, true, false, false, false, true, false, null, Quality.STUNNED, null);

		// ABILITY 3
		test = buildAbilities(test, true, false, false, false, Cost.twoArc, 4, "Geas", "/assets/geddy3.png",
				"Geddy gives one unit a quest, causing them to be stunned and unable to become invulnerable for 2 turns.",
				effects2, null, null, null, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Distracting Performance",
				"Geddy is being loud.  He's invulnerable.", "/assets/geddy4.png", true, false, false, false, false,
				true, false, null, Quality.INVULNERABLE, null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Distracting Performance",
				"/assets/geddy4.png", "Geddy makes a scene, going invulnerable", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(4);
		c.setCharacterId(4);
		c.setName("Geddy Splintwalker");
		c.setAvatarUrl("/assets/geddy.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.GEDDY);
		facts.add(Faction.OUTSIDERS);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

	private Combatant makeTristane() {
		// phys / mag / affl / inter / cond / vis / stack
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 1, "Backstab", "This unit took damage from backstab",
				"/assets/tristane1.png", true, false, false, false, false, true, false, buildStat(Stat.DAMAGE, 40),
				null, null);

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, false, Cost.threeRan, 1, "Backstab",
				"/assets/tristane1.png",
				"Tristane sneaks behind an enemy, stabbing them in the back for 40 physical damage.", effects, null,
				null, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 4, "Guildmaster", "Tristane's costs have been reduced by one.",
				"/assets/tristane2.png", true, false, false, false, false, true, false, buildStat(Stat.COST_CHANGE, -1),
				null, null);

		// ABILITY 2
		test = buildAbilities(test, false, false, true, false, Cost.twoRan, 1, "Guildmaster", "/assets/tristane2.png",
				"Tristane calls in backup, reducing the costs of his abilities by one for three turns.", null, effects1,
				null, null, null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 1, "Scheme",
				"If this unit uses a damaging skill, it will be countered.", "/assets/tristane3.png", true, false,
				false, false, true, false, false, null, Quality.COUNTERED, Conditional.TARGET_DID(Conditional.DAMAGE));

		// ABILITY 3
		test = buildAbilities(test, true, false, false, false, Cost.twoRan, 4, "Scheme", "/assets/tristane3.png",
				"Tristane schemes against a unit, countering any damaging skill for one turn.", effects2, null, null,
				null, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Untrackable", "This unit is invulnerable.",
				"/assets/tristane4.png", true, false, false, false, false, true, false, null, Quality.INVULNERABLE,
				null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Untrackable", "/assets/tristane4.png",
				"Tristane goes invulnerable, unable to be found.", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(5);
		c.setCharacterId(5);
		c.setName("Guildmaster Tristane");
		c.setAvatarUrl("/assets/tristane.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.TRISTANE);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}

//Drundar 
//
//CD: 1 - Hammer Down - Piercing damage
//CD: 4 - Build Up - put shields or armor on self
//CD: 4 - Quake - aoe dmg, and shield destruction
//CD: 4 - Call The Guard - Go invulnerable for one turn
	private Combatant makeDrundar() {
		// phys / mag / affl / inter / cond / vis / stack
		// EFFECTS 1
		List<BattleEffect> effects = buildEffects(null, 1, "Hammer Down", "This unit took damage from hammer down",
				"/assets/drundar1.png", true, false, false, false, false, true, false,
				buildStat(Stat.PIERCING_DAMAGE, 35), null, null);

		// ABILITY 1
		List<Ability> test = buildAbilities(null, true, false, false, false, Cost.oneStrOneRan, 1, "Hammer Down",
				"/assets/drundar1.png", "Drundar uses his magical hammer to deal 35 piercing damage.", effects, null,
				null, null, null);

		// EFFECTS 2
		List<BattleEffect> effects1 = buildEffects(null, 3, "Manor Security", "Drundar has 50% physical armor.",
				"/assets/drundar2.png", true, false, false, false, false, true, false,
				buildStat(Stat.PHYSICAL_ARMOR, 50), null, null);

		// ABILITY 2
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 1, "Manor Security", "/assets/drundar2.png",
				"Drundar beefs up on security, taking 50% less physical damage for 3 turns", null, effects1, null, null,
				null);

		// EFFECTS 3
		List<BattleEffect> effects2 = buildEffects(null, 1, "Quake",
				"This unit has taken damage and lost all shields from quake.", "/assets/drundar3.png", false, true,
				false, false, false, true, false, buildStat(Stat.SHIELD_GAIN, -9999), null, null);

		effects2 = buildEffects(effects2, 1, "Quake", "This unit has taken damage and lost all shields from quake.",
				"/assets/drundar3.png", false, true, false, false, false, true, false, buildStat(Stat.DAMAGE, 20), null,
				null);

		// ABILITY 3
		test = buildAbilities(test, true, false, false, true, Cost.oneStrOneArc, 4, "Quake", "/assets/drundar3.png",
				"Drundar creates a magical quake, destroying all enemy shields and doing 20 damage to all enemies.",
				null, null, null, effects2, null);

		// EFFECTS 4
		List<BattleEffect> effects3 = buildEffects(null, 1, "Brotherly Bond", "This unit is invulnerable.",
				"/assets/drundar4.png", true, false, false, false, false, true, false, null, Quality.INVULNERABLE,
				null);

		// ABILITY 4
		test = buildAbilities(test, false, false, true, false, Cost.oneRan, 4, "Brotherly Bond", "/assets/drundar4.png",
				"Drundar goes invulnerable.", null, effects3, null, null, null);

		Combatant c = new Combatant();
		c.setId(6);
		c.setCharacterId(6);
		c.setName("Drundar Earthbreaker");
		c.setAvatarUrl("/assets/drundar.png");
		c.setAbilities(test);
		List<String> facts = new ArrayList<>();
		facts.add(Faction.DRUNDAR);
		c.setFactions(facts);

		c = this.characterService.save(c);
		return c;
	}
//
//  Kalio 
//
//  CD: 1 - Mage Armor - Give ally or self armor and reflect damage
//  CD: 4 - Dispel - remove magical effects
//  CD: 4 - Research - reroll energy hanatarou style
//  CD: 4 - A+ - Go invulnerable for one turn
//
//  Ralion 
//
//  CD: 1 - Extort - increase enemy costs
//  CD: 4 - Lightning Bolt - hard dmg
//  CD: 4 - Arrogant Generosity - heal enemy for 10 hp, lower enemy damage, or/and stun skills
//  CD: 4 - Ultimatum - Go invulnerable for one turn
//
//  Imperia 
//
//  CD: 1 - Sneaky Jump - Counter skills used on her
//  CD: 4 - Stunning Fist - stun and dmg, if sneak jump succeeded then bonus
//  CD: 4 - Flurry of Blows - dot, if sneak jump succeeded then bonus
//  CD: 4 - 1 Random - Go invulnerable for one turn
//
//  Millor 
//
//  CD: 1 - Hold Hostage - Stun an enemy's physical skills and put shields on self
//  CD: 4 - Ice Trap - put counter on enemy, do damage to them and counter if they use a damaging skill
//  CD: 4 - Mutilate - dmg and increase costs on one unit
//  CD: 4 - 1 Random - Go invulnerable for one turn
}
