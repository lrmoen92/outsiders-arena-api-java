package org.outsiders.arena.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleEffect;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.Combatant;
import org.outsiders.arena.domain.CostCheckDTO;
import org.outsiders.arena.domain.Energy;
import org.outsiders.arena.domain.Mission;
import org.outsiders.arena.domain.MissionProgress;
import org.outsiders.arena.domain.MissionRequirement;
import org.outsiders.arena.domain.Player;
import org.outsiders.arena.domain.Quality;
import org.outsiders.arena.domain.Stat;
import org.outsiders.arena.service.BattleService;
import org.outsiders.arena.service.CharacterService;
import org.outsiders.arena.service.MissionService;
import org.outsiders.arena.service.PlayerService;
import org.outsiders.arena.util.NRG;
import org.outsiders.arena.util.TurnResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class BattleMessageService {

	public static Logger LOG = LoggerFactory.getLogger(BattleMessageService.class);

	@Autowired
	protected BattleService battleService;

	@Autowired
	protected PlayerService playerService;

	@Autowired
	protected CharacterService characterService;

	@Autowired
	protected MissionService missionService;

	protected ObjectMapper mapper = new ObjectMapper();

	public Map<String, Integer> subtractMap(Map<String, Integer> map, Map<String, Integer> mapToSubtract) {
		Map<String, Integer> resultMap = new HashMap<>();
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			int energyLeft = e.getValue() - mapToSubtract.get(e.getKey());
			resultMap.put(e.getKey(), energyLeft);
		}
		return resultMap;
	}

	public Map<String, Integer> copyMap(Map<String, Integer> map) {
		Map<String, Integer> resultMap = new HashMap<>();
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			resultMap.put(e.getKey(), e.getValue());
		}
		return resultMap;
	}

	private Integer getMapEntryAsInt(String key, Map m) {
		return ((Double) m.get(key)).intValue();
	}

	public String handleMatchmakingMessage(Map valueMap) {
		Integer characterId1 = getMapEntryAsInt("char1", valueMap);
		Integer characterId2 = getMapEntryAsInt("char2", valueMap);
		Integer characterId3 = getMapEntryAsInt("char3", valueMap);
		Integer playerId = getMapEntryAsInt("playerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
		String queue = valueMap.get("queue").toString();
		Battle battle = this.battleService.getByArenaId(arenaId);
		if (battle == null) {
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

			battle.setStatus("STARTING");

			Battle savedBattle = this.battleService.save(battle);

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

	public String handleEnergyTradeMessage(Map valueMap) throws Exception {
		LOG.info("Energy Trade");
		// TODO:
		// this can be cleaned up with object mapper
		int playerId = getMapEntryAsInt("playerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
		Battle b = battleService.getByArenaId(arenaId);
		List<String> m = (List) valueMap.get("spent");
		String chosen = valueMap.get("chosen").toString();

		if (b.getPlayerIdOne() == playerId) {
			for (String s : m) {
				int newVal = b.getPlayerOneEnergy().get(s) - 1;
				b.getPlayerOneEnergy().put(s, newVal);
			}

			int i = b.getPlayerOneEnergy().get(chosen);
			b.getPlayerOneEnergy().put(chosen, i + 1);
		} else {
			for (String s : m) {
				int newVal = b.getPlayerTwoEnergy().get(s) - 1;
				b.getPlayerTwoEnergy().put(s, newVal);
			}

			int i = b.getPlayerTwoEnergy().get(chosen);
			b.getPlayerTwoEnergy().put(chosen, i + 1);
		}

		b = battleService.save(b);
		String responseJson = "{\"type\": \"ETRADE\", \"playerId\": " + playerId + ", \"battle\": "
				+ new Gson().toJson(b) + "}";
		return responseJson;
	}

	public String handleTurnEndMessage(Map valueMap) throws Exception {
		LOG.info("Turn End");
		Integer playerId = getMapEntryAsInt("playerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
		Battle b = battleService.getByArenaId(arenaId);
		boolean isPlayerOne = playerId == b.getPlayerIdOne();
		BattleTurnDTO dto = mapper.convertValue(valueMap.get("battleTurnDTO"), BattleTurnDTO.class);

		// do battle logic here
		Battle bPost = new TurnResolver().handleTurns(b, dto, isPlayerOne);

		bPost.setStatus("PLAYING");
		bPost = battleService.save(bPost);

		LOG.info(bPost.toString());
		String responseJson = "{\"type\": \"END\", \"isPlayerOne\": " + isPlayerOne + ", \"battle\": "
				+ new Gson().toJson(bPost) + "}";
		return responseJson;
	}

	public String handleTargetCheckMessage(Map valueMap) {
		LOG.info("Target Check");
		Integer playerId = getMapEntryAsInt("playerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
		Battle b = battleService.getByArenaId(arenaId);
		AbilityTargetDTO dto = mapper.convertValue(valueMap.get("abilityTargetDTO"), AbilityTargetDTO.class);

		int charPos = dto.getCharacterPosition();
		// this should be empty VV
		// set it up to return everything, and filter it below
		List<Integer> tarPos = new ArrayList<>();

		int teamPos;
		tarPos.add(0, -1);
		tarPos.add(1, -1);
		tarPos.add(2, -1);
		tarPos.add(3, -1);
		tarPos.add(4, -1);
		tarPos.add(5, -1);

		boolean isPlayerOne = b.getPlayerIdOne() == playerId;

		List<Combatant> team;
		List<Combatant> enemyTeam;

		if (isPlayerOne) {
			team = b.getPlayerOneTeam();
			enemyTeam = b.getPlayerTwoTeam();
			teamPos = charPos;
		} else {
			team = b.getPlayerTwoTeam();
			enemyTeam = b.getPlayerOneTeam();
			teamPos = charPos - 3;
		}

		Ability a = dto.getAbility();
		if (a.isAoe()) {
			if (a.isEnemy()) {
				for (Combatant ch : enemyTeam) {
					if (!ch.isDead()) {
						if (this.canBeTargeted(ch.getEffects(), a.getAoeEnemyEffects())) {
							tarPos.set(ch.getPosition(), ch.getPosition());
						}
					}
				}
			}
			if (a.isAlly()) {
				// do whole team here, null it out later in self if not the case
				for (Combatant ch : team) {
					if (!ch.isDead()) {
						tarPos.set(ch.getPosition(), ch.getPosition());
					}
				}
			}
			if (!a.isSelf()) {
				tarPos.set(charPos, -1);
				// just null out location at parentIndex or whatever from the dto because this
				// is AOE and covered above in ally
			}
		} else {
			// SINGLE TARGET
			if (a.isEnemy()) {
				for (Combatant ch : enemyTeam) {
					if (!ch.isDead()) {
						if (this.canBeTargeted(ch.getEffects(), a.getEnemyEffects())) {
							tarPos.set(ch.getPosition(), ch.getPosition());
						}
					}
				}
			}
			if (a.isAlly()) {
				// do whole team here
				for (Combatant ch : team) {
					if (!ch.isDead()) {
						tarPos.set(ch.getPosition(), ch.getPosition());
					}
				}
				// remove self from ally list
				tarPos.set(charPos, -1);
			}
			if (a.isSelf()) {
				Combatant ch = team.get(teamPos);
//        		boolean canBe = this.canBeTargeted(ch.getEffects(), a.getSelfEffects());
				tarPos.set(ch.getPosition(), ch.getPosition());
				// just null out location at parentIndex or whatever from the dto
			}
		}

		// TODO: V build the object and throw it in here, front end should be good

		dto.setTargetPositions(tarPos);

		String responseJson = "{\"type\": \"TCHECK\", \"playerId\": " + playerId + ", \"dto\": "
				+ new Gson().toJson(dto) + "}";
		return responseJson;
	}

	public String handleCostCheckMessage(Map valueMap) throws Exception {
		// respond with an array of the abilities that CAN be cast
		// currently responding with all
		Integer[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

		LOG.info("Cost Check");
		Integer playerId = getMapEntryAsInt("playerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
		Battle b = battleService.getByArenaId(arenaId);

		CostCheckDTO dto = mapper.convertValue(valueMap.get("costCheckDTO"), CostCheckDTO.class);

		List<List<String>> allAbilitiesCosts = dto.getAllyCosts();
		List<AbilityTargetDTO> chosenAbilities = dto.getChosenAbilities();
		List<String> spentEnergyList = dto.getSpentEnergy();

		// backend should know proper energy, just update the battle and save after each
		// call.

		Map<String, Integer> spentEnergy = new HashMap<>();
		Map<String, Integer> currentEnergy;
		List<Combatant> units;

		if (playerId == b.getPlayerIdOne()) {
			units = b.getPlayerOneTeam();
			currentEnergy = b.getPlayerOneEnergy();
		} else {
			units = b.getPlayerTwoTeam();
			currentEnergy = b.getPlayerTwoEnergy();
		}

		currentEnergy.put(Energy.RANDOM, 0);
		spentEnergy.put(Energy.RANDOM, 0);
		spentEnergy.put(Energy.STRENGTH, 0);
		spentEnergy.put(Energy.DEXTERITY, 0);
		spentEnergy.put(Energy.ARCANA, 0);
		spentEnergy.put(Energy.DIVINITY, 0);
		for (String s : spentEnergyList) {
			int old = spentEnergy.get(s);
			spentEnergy.put(s, old + 1);
		}
		Integer[] charPos = { 0, 1, 2 };

		if (chosenAbilities != null) {
			for (AbilityTargetDTO abilityTargetDto : chosenAbilities) {
				Ability ability = abilityTargetDto.getAbility();

				// this sets character to unusable
				charPos[abilityTargetDto.getCharacterPosition()] = -1;
			}
		}

		Integer[] ab = this.checkIfStunned(a, units);
		Integer[] c = this.checkIfCharacterIsDead(ab, units);
		Integer[] d = this.checkIfCharacterActed(charPos, c);
		Integer[] e = this.checkCooldowns(units, d);

		// remove spent energy from current energy to see what real current is
		Map<String, Integer> availableEnergyOriginal = this.subtractMap(currentEnergy, spentEnergy);

		int indexCounter = 0;

		for (List<String> abilityCost : allAbilitiesCosts) {

			Map<String, Integer> availableEnergy = this.copyMap(availableEnergyOriginal);
			boolean goodToGo = true;

			// check cost on ability
			for (String string : abilityCost) {
				if (string.equals(Energy.RANDOM)) {
					int oldVal = availableEnergy.get(Energy.RANDOM);
					availableEnergy.put(Energy.RANDOM, oldVal - 1);
				} else if (string.equals(Energy.STRENGTH)) {
					int oldVal = availableEnergy.get(Energy.STRENGTH);
					if (oldVal > 0) {
						availableEnergy.put(Energy.STRENGTH, oldVal - 1);
					} else {
						goodToGo = false;
					}
				} else if (string.equals(Energy.DEXTERITY)) {
					int oldVal = availableEnergy.get(Energy.DEXTERITY);
					if (oldVal > 0) {
						availableEnergy.put(Energy.DEXTERITY, oldVal - 1);
					} else {
						goodToGo = false;
					}
				} else if (string.equals(Energy.ARCANA)) {
					int oldVal = availableEnergy.get(Energy.ARCANA);
					if (oldVal > 0) {
						availableEnergy.put(Energy.ARCANA, oldVal - 1);
					} else {
						goodToGo = false;
					}
				} else if (string.equals(Energy.DIVINITY)) {
					int oldVal = availableEnergy.get(Energy.DIVINITY);
					if (oldVal > 0) {
						availableEnergy.put(Energy.DIVINITY, oldVal - 1);
					} else {
						goodToGo = false;
					}
				}

				if (!goodToGo) {
					break;
				}
			}
			int workingTotal = this.getTotalForEnergy(availableEnergy);
			boolean hasProperTotal = workingTotal >= 0;

			if (hasProperTotal && goodToGo) {
				// ability is good to go
			} else {
				e[indexCounter] = -1;
			}
			indexCounter++;
		}

		String responseJson = "{\"type\": \"CCHECK\", \"playerId\": " + playerId + ", \"usable\": "
				+ new Gson().toJson(e) + "}";
		LOG.info(responseJson.toString());
		return responseJson;
	}

	public String handleGameEndMessage(Map valueMap) throws Exception {

		LOG.info("Game End");

		Integer loserId = getMapEntryAsInt("loserId", valueMap);
		Integer winnerId = getMapEntryAsInt("winnerId", valueMap);
		Integer arenaId = getMapEntryAsInt("arenaId", valueMap);

		Player loser = this.playerService.findById(loserId).get();
		Player winner = this.playerService.findById(winnerId).get();

		Battle battle = this.battleService.getByArenaId(arenaId);
		String queue = battle.getQueue();
		battle.setStatus("ENDED");
		Battle saved = this.battleService.save(battle);

		boolean playerOneWon = battle.getPlayerIdOne() == winner.getId();
		List<Mission> missions = (List<Mission>) this.missionService.findAll();
		List<Combatant> winningTeam = playerOneWon ? battle.getPlayerOneTeam() : battle.getPlayerTwoTeam();
		List<Combatant> losingTeam = playerOneWon ? battle.getPlayerTwoTeam() : battle.getPlayerOneTeam();
		this.battleService.delete(battle);

		int xpLost = 0;
		int xpGain = 0;
		boolean lvLost = false;
		boolean lvGain = false;

		if (queue.equals("LADDER")) {
			int oldLv1 = winner.getLevel();
			int oldLv2 = loser.getLevel();

			xpLost = loser.loseBattleXP(winner);
			xpGain = winner.winBattleXP(loser);

			int newLv1 = winner.getLevel();
			int newLv2 = loser.getLevel();

			lvLost = newLv2 < oldLv2;
			lvGain = newLv1 > oldLv1;
		}

		// TODO: CHECK MISSIONS HERE
		boolean missionProgress = false;
		boolean newMissionProgress = false;
		StringBuilder missionString = new StringBuilder();
		boolean characterUnlocked = false;
		StringBuilder characterString = new StringBuilder();
		if (!queue.equals("PRIVATE")) {

			List<String> winningFacts = new ArrayList<>();
			List<String> losingFacts = new ArrayList<>();
			winningFacts.add("ANYONE");
			losingFacts.add("ANYONE");

			for (Combatant c : winningTeam) {
				c.getFactions().forEach(f -> {
					winningFacts.add(f);
				});
			}

			for (Combatant c : losingTeam) {
				c.getFactions().forEach(f -> {
					losingFacts.add(f);
				});
			}

			List<Integer> currentMissions = new ArrayList<>();
			List<Integer> missionsToMarkDone = new ArrayList<>();
			for (MissionProgress mp : winner.getMissionProgress()) {
				boolean finished = true;
				boolean characterUnlock = false;
				Mission target = null;
				currentMissions.add(mp.getMissionId());
				for (MissionRequirement req : mp.getRequirements()) {
					boolean reqFinished = true;
					for (Mission mis : missions) {
						if (mp.getMissionId() == mis.getId()) {
							target = mis;
						}
					}
					if (winningFacts.contains(req.getUserFaction()) && losingFacts.contains(req.getTargetFaction())) {
						int old = req.getAmount();
						int cur = old + 1;
						int idUnlock = target.getCharacterIdUnlocked();
						characterUnlock = idUnlock > 0;
						for (MissionRequirement tar : target.getRequirements()) {
							if (req.getTargetFaction().equals(tar.getTargetFaction())
									&& req.getUserFaction().equals(tar.getUserFaction())) {
								if (cur != tar.getAmount()) {
									reqFinished = false;
								}
								if (!(cur > tar.getAmount())) {
									missionProgress = true;
									missionString.append("Won a game with " + req.getUserFaction().toLowerCase()
											+ " vs. " + req.getTargetFaction().toLowerCase() + " for mission : "
											+ target.getName() + "\\r\\n");
									req.setAmount(cur);
								}
							}
						}
					} else {
						for (MissionRequirement tar : target.getRequirements()) {
							if (req.getTargetFaction().equals(tar.getTargetFaction())
									&& req.getUserFaction().equals(tar.getUserFaction())) {
								if (req.getAmount() == tar.getAmount()) {
									reqFinished = true;
								} else {
									reqFinished = false;
								}
							}
						}
					}
					if (!reqFinished) {
						finished = false;
					}
				}

				if (finished) {
					missionsToMarkDone.add(mp.getMissionId());
					if (characterUnlock) {
						characterUnlocked = true;
						winner.getCharacterIdsUnlocked().add(target.getCharacterIdUnlocked());
						characterString.append("Character ID: " + target.getCharacterIdUnlocked() + "\\r\\n");
					} else {
						if (missionProgress) {
							// i dont think do anything
						}
					}
				}
			}

			for (Integer i : missionsToMarkDone) {
				winner.getMissionIdsCompleted().add(i);

				winner.getMissionProgress().removeIf((x) -> {
					return x.getMissionId() == i;
				});
			}

			missionsToMarkDone.clear();

			// check undone missions
			for (Mission m : missions) {
				boolean finished = false;
				boolean newCharacterUnlock = false;
				List<MissionRequirement> missionReqProgressHolder = new ArrayList<>();
				// i haven't done it yet, and i'm not currently working on it
				if (!winner.getMissionIdsCompleted().contains(m.getId()) && !currentMissions.contains(m.getId())) {

					// i'm the right level to do it
					if (m.getMinmumLevel() < winner.getLevel()) {

						// if there's a prereq
						if (m.getPrerequisiteMissionId() > 0) {
							// I better have done it
							if (winner.getMissionIdsCompleted().contains(m.getPrerequisiteMissionId())) {

								for (MissionRequirement req : m.getRequirements()) {
									boolean reqFinished = true;
									if (winningFacts.contains(req.getUserFaction())
											&& losingFacts.contains(req.getTargetFaction())) {
										int old = 0;
										int cur = old + 1;
										int idUnlock = m.getCharacterIdUnlocked();
										newCharacterUnlock = idUnlock > 0;

										if (cur != req.getAmount()) {
											reqFinished = false;
										}
										// build mission progress object
										if (!(cur > req.getAmount())) {
											newMissionProgress = true;
											missionString.append("Won a game with " + req.getUserFaction().toLowerCase()
													+ " vs. " + req.getTargetFaction().toLowerCase() + " for mission : "
													+ m.getName() + "\\r\\n");
										}
										MissionRequirement newReq = new MissionRequirement(cur, req);
										missionReqProgressHolder.add(newReq);
									} else {
										MissionRequirement newReq = new MissionRequirement(0, req);
										missionReqProgressHolder.add(newReq);
										reqFinished = false;
									}
									if (!reqFinished) {
										finished = false;
									}
								}

							}
							// no prerequisite
						} else {

							for (MissionRequirement req : m.getRequirements()) {
								boolean reqFinished = true;
								if (winningFacts.contains(req.getUserFaction())
										&& losingFacts.contains(req.getTargetFaction())) {
									int old = 0;
									int cur = old + 1;
									int idUnlock = m.getCharacterIdUnlocked();
									newCharacterUnlock = idUnlock > 0;

									if (cur != req.getAmount()) {
										reqFinished = false;
									}
									// build mission progress object
									if (!(cur > req.getAmount())) {
										newMissionProgress = true;
										missionString.append("Won a game with " + req.getUserFaction().toLowerCase()
												+ " vs. " + req.getTargetFaction().toLowerCase() + " for mission : "
												+ m.getName() + "\\r\\n");
									}
									MissionRequirement newReq = new MissionRequirement(cur, req);
									missionReqProgressHolder.add(newReq);
								} else {
									MissionRequirement newReq = new MissionRequirement(0, req);
									missionReqProgressHolder.add(newReq);
									reqFinished = false;
								}
								if (!reqFinished) {
									finished = false;
								}
							}
						}

					}

				}

				if (finished) {
					missionsToMarkDone.add(m.getId());
					if (newCharacterUnlock) {
						characterUnlocked = true;
						winner.getCharacterIdsUnlocked().add(m.getCharacterIdUnlocked());
						characterString.append("Character ID: " + m.getCharacterIdUnlocked() + "\\r\\n");
					}
				} else {
					if (newMissionProgress) {
						MissionProgress prog = new MissionProgress(m, missionReqProgressHolder);
						List<MissionProgress> oldProg = winner.getMissionProgress();
						oldProg.add(prog);
					}
				}
			}

			for (Integer i : missionsToMarkDone) {
				winner.getMissionIdsCompleted().add(i);

				winner.getMissionProgress().removeIf((x) -> {
					return x.getMissionId() == i;
				});
			}

			missionsToMarkDone.clear();

		}

		Player newLoser = this.playerService.save(loser);
		Player newWinner = this.playerService.save(winner);

		String winnerString;
		String loserString;

		StringBuilder sbw = new StringBuilder();
		StringBuilder sbl = new StringBuilder();

		sbw.append("CONGRATULATIONS!  You've won a " + queue.toLowerCase() + " battle against " + loser.getDisplayName()
				+ ". You've gained " + xpGain + " experience" + (lvGain ? ", and ranked up!" : "!"));
		sbl.append("BETTER LUCK NEXT TIME!  You've lost a " + queue.toLowerCase() + " battle against "
				+ winner.getDisplayName() + ". You've lost " + xpLost + " experience"
				+ (lvLost ? ", and demoted..." : "!"));

		if (missionProgress || newMissionProgress) {

			// TODO CHECK MISSION PROGRESS

//            sbw.append("\\r\\n");
			sbw.append("Progress made on the following missions: " + missionString.toString());
		}

		if (characterUnlocked) {

			// TODO CHECK CHARACTER PROGRESS
//            sbw.append("\\r\\n");
			sbw.append("New Character Unlocked!  " + characterString.toString());
		}

		winnerString = "\"" + sbw.toString() + "\"";
		loserString = "\"" + sbl.toString() + "\"";

		// TODO: can verify here playerOneVictory(), or two, depending on the ids

		String responseJson = "{\"type\": \"GAME_END\", \"winner\": " + new Gson().toJson(newWinner) + ", \"loser\": "
				+ new Gson().toJson(newLoser) + ", \"winnerString\": " + winnerString + ", \"loserString\": "
				+ loserString + "}";
		LOG.info(responseJson.toString());
		return responseJson;
	}

	public Integer[] checkIfStunned(Integer[] input, List<Combatant> team) {
		for (int i = 0; i < 3; i++) {
			Combatant combatant = team.get(i);
			boolean stunned = false;
			boolean physStunned = false;
			boolean magStunned = false;

			for (BattleEffect e : combatant.getEffects()) {
				if (Quality.STUNNED.equals(e.getQuality())) {
					// might have to check conditionals here
					// TODO: CONDITIONALS
					stunned = true;
				} else if (Quality.PHYSICAL_STUNNED.equals(e.getQuality())) {
					physStunned = true;
				} else if (Quality.MAGICAL_STUNNED.equals(e.getQuality())) {
					magStunned = true;
				}
			}

			if (stunned) {
				input[0 + (4 * i)] = -1;
				input[1 + (4 * i)] = -1;
				input[2 + (4 * i)] = -1;
				input[3 + (4 * i)] = -1;
			} else {
				if (physStunned) {
					for (Ability a : combatant.getAbilities()) {
						if (a.isPhysical()) {
							input[a.getPosition() + (4 * i)] = -1;
						}
					}
				}
				if (magStunned) {
					for (Ability a : combatant.getAbilities()) {
						if (a.isMagical()) {
							input[a.getPosition() + (4 * i)] = -1;
						}
					}
				}
			}

		}
		return input;
	}

	public Integer[] checkIfCharacterIsDead(Integer[] input, List<Combatant> team) {
		boolean characterOneIsDead = team.get(0).isDead();
		boolean characterTwoIsDead = team.get(1).isDead();
		boolean characterThreeIsDead = team.get(2).isDead();

		for (int i = 0; i < input.length; i++) {
			if (characterOneIsDead) {
				if (-1 < input[i] && input[i] < 4) {
					input[i] = -1;
				}
			}
			if (characterTwoIsDead) {
				if (3 < input[i] && input[i] < 8) {
					input[i] = -1;
				}
			}
			if (characterThreeIsDead) {
				if (7 < input[i] && input[i] < 12) {
					input[i] = -1;
				}
			}
		}
		return input;
	}

	public Integer[] checkIfCharacterActed(Integer[] position, Integer[] input) {
		for (int i = 0; i < 3; i++) {
			if (position[i] == -1) {
				input[0 + (i * 4)] = -1;
				input[1 + (i * 4)] = -1;
				input[2 + (i * 4)] = -1;
				input[3 + (i * 4)] = -1;
			}
		}
		return input;
	}

	public Integer[] checkCooldowns(List<Combatant> units, Integer[] input) {
		int counter = 0;

		for (Combatant ch : units) {
			for (int cd : ch.getCooldowns()) {
				// this is specific to return CD length in a cryptic way i guess lol
				if (cd > 0) {
					input[counter] = -1 - cd;
				}
				counter++;
			}
		}
		return input;
	}

	public int getTotalForEnergy(Map<String, Integer> map) {
		int counter = 0;
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			counter = counter + e.getValue();
		}
		return counter;
	}

	public boolean canBeTargeted(List<BattleEffect> existingEffects, List<BattleEffect> targetingEffects) {
		boolean canBe = true;

		for (BattleEffect effect : existingEffects) {
			// if enemy is invulnerable
			if (Quality.INVULNERABLE.equals(effect.getQuality())) {
				// check for invuln piercing
				for (BattleEffect targetingEffect : targetingEffects) {
					if (targetingEffect.getStatMods() != null) {
						if (targetingEffect.getStatMods().get(Stat.TRUE_DAMAGE) == null) {
							canBe = false;
						} else {
							canBe = true;
							break;
						}
					} else {
						canBe = false;
					}
				}
				// check for vulnerable which cancels
				for (BattleEffect existingEffect : existingEffects) {
					if (Quality.VULNERABLE.equals(existingEffect.getQuality())) {
						canBe = true;
						break;
					}
				}
			}
		}

		for (BattleEffect effect : existingEffects) {
			if (Quality.UNTARGETABLE.equals(effect.getQuality())) {
				// check for untargetable
				canBe = false;
				// check for targetable which cancels
				for (BattleEffect existingEffect : existingEffects) {
					if (Quality.TARGETABLE.equals(existingEffect.getQuality())) {
						canBe = true;
						break;
					}
				}
			}
		}

		return canBe;
	}
}
