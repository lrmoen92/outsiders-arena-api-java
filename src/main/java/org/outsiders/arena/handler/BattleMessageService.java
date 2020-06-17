package org.outsiders.arena.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.CharacterInstance;
import org.outsiders.arena.domain.CostCheckDTO;
import org.outsiders.arena.domain.Effect;
import org.outsiders.arena.domain.Energy;
import org.outsiders.arena.domain.Player;
import org.outsiders.arena.domain.Quality;
import org.outsiders.arena.domain.Stat;
import org.outsiders.arena.service.BattleService;
import org.outsiders.arena.service.CharacterService;
import org.outsiders.arena.service.PlayerService;
import org.outsiders.arena.util.NRG;
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
    protected NRG nrg;
    
    protected ObjectMapper mapper = new ObjectMapper();
    
    private Integer getMapEntryAsInt(String key, Map m) {
    	return ((Double) m.get(key)).intValue();
    }
    
    private String getMapEntryAsString(String key, Map m) {
    	return m.get(key).toString();
    }

    public String handleMatchmakingMessage(Map valueMap) {
        Battle savedBattle = null;
        Integer characterId1 = getMapEntryAsInt("char1", valueMap);
        Integer characterId2 = getMapEntryAsInt("char2", valueMap);
        Integer characterId3 = getMapEntryAsInt("char3", valueMap);
        Integer playerId = getMapEntryAsInt("playerId", valueMap);
        Integer arenaId = getMapEntryAsInt("arenaId", valueMap);
        String opponentName = getMapEntryAsString("opponentName", valueMap);
    	Battle battle = this.battleService.getByPlayerDisplayName(opponentName);
        if (battle == null) {
	        battle = new Battle();
	        battle.setId(this.nrg.randomInt());
	        battle.setArenaId(arenaId);
	        battle.setPlayerIdOne(playerId.intValue());
	        savedBattle = this.battleService.save(battle);
	        ArrayList<CharacterInstance> list1 = new ArrayList<CharacterInstance>();
	        CharacterInstance i1 = new CharacterInstance();
	        CharacterInstance i2 = new CharacterInstance();
	        CharacterInstance i3 = new CharacterInstance();
	        i1.setCharacterId(characterId1.intValue());
	        i2.setCharacterId(characterId2.intValue());
	        i3.setCharacterId(characterId3.intValue());
            i1.setPosition(0);
            i2.setPosition(1);
            i3.setPosition(2);
	        list1.add(i1);
	        list1.add(i2);
	        list1.add(i3);
	        battle.setPlayerOneTeam(list1);

	        savedBattle = this.battleService.save(battle);
	        LOG.info("SAVED BATTLE:: " + savedBattle.toString());
	        return new Gson().toJson("WAITING FOR OPPONENTS");
        } else {
	        ArrayList<CharacterInstance> list1 = new ArrayList<CharacterInstance>();
            CharacterInstance i1 = new CharacterInstance();
            CharacterInstance i2 = new CharacterInstance();
            CharacterInstance i3 = new CharacterInstance();
            i1.setCharacterId(characterId1.intValue());
            i2.setCharacterId(characterId2.intValue());
            i3.setCharacterId(characterId3.intValue());
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

            savedBattle = this.battleService.save(battle);
            List<Character> characters = characterService.getCharactersForBattle(savedBattle);

            
            LOG.info("SAVED BATTLE:: " + savedBattle.toString());
            
            Player playerOne = this.playerService.findById(Integer.valueOf(battle.getPlayerIdOne())).get();
        	Player playerTwo = this.playerService.findById(Integer.valueOf(battle.getPlayerIdTwo())).get();
            LOG.info("Match Made between {} and {}", playerOne.getDisplayName(), playerTwo.getDisplayName());
            String characterJson = new Gson().toJson(characters);
            String battleJson = new Gson().toJson(savedBattle);
            String playerOneJson = new Gson().toJson(playerOne);
            String playerTwoJson = new Gson().toJson(playerTwo);
            
            if (battleJson != null && playerOne != null && playerTwo != null && characterJson != null) {
                String responseJson = "{\"type\": \"INIT\", \"battle\": " + battleJson + ",\"playerOne\": " + playerOneJson + ",\"playerTwo\": " + playerTwoJson + ",\"characters\": " + characterJson + "}";
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
        Map<String, Double> m = (Map) valueMap.get("spent");
        String chosen = valueMap.get("chosen").toString();
        Battle b = battleService.getByPlayerId(playerId);
        
        if(b.getPlayerIdOne() == playerId) {
        	for (Entry<String, Double> e : m.entrySet()) {
        		String energy = e.getKey();
        		int amount = (int) Math.round(e.getValue());
        		int newVal = b.getPlayerOneEnergy().get(energy) - amount;
        		if (newVal < 0) {
        			throw new Exception();
        		}
        		b.getPlayerOneEnergy().put(energy,  newVal);
        	}
        	
        	int i =b.getPlayerOneEnergy().get(chosen);
        	b.getPlayerOneEnergy().put(chosen, i+1);
        } else {
        	
        	for (Entry<String, Double> e : m.entrySet()) {
        		String energy = e.getKey();
        		int amount = (int) Math.round(e.getValue());
        		int newVal = b.getPlayerTwoEnergy().get(energy) - amount;
        		if (newVal < 0) {
        			throw new Exception();
        		}
        		b.getPlayerTwoEnergy().put(energy,  newVal);
        	}
        	
        	int i =b.getPlayerTwoEnergy().get(chosen);
        	b.getPlayerTwoEnergy().put(chosen, i+1);
    	}
        
        b = battleService.save(b);
        String responseJson = "{\"type\": \"ETRADE\", \"playerId\": " + playerId + ", \"battle\": " + new Gson().toJson(b) + "}";
        return responseJson;
    }
    
    public String handleTurnEndMessage(Map valueMap) throws Exception {
        LOG.info("Turn End");
        Integer playerId = getMapEntryAsInt("playerId", valueMap);
        Battle b = battleService.getByPlayerId(playerId);
        boolean isPlayerOne = playerId == b.getPlayerIdOne();
        BattleTurnDTO dto = mapper.convertValue(valueMap.get("battleTurnDTO"), BattleTurnDTO.class);
        
        //first time this happens is the record of the "end" of turn 1

        b.setTurn(b.getTurn() + 1);
        // check if duration is 0, if so, remove it
        

        // do battle logic here
        Battle bPost = nrg.handleTurns(b, dto, isPlayerOne);
        
        // countdown cooldowns by one
        if (isPlayerOne) {
	        for (CharacterInstance c: bPost.getPlayerOneTeam()) {
	        	int one = c.getCooldownOne();
	        	int two = c.getCooldownTwo();
	        	int three = c.getCooldownThree();
	        	int four = c.getCooldownFour();
	        	if (one > 0) {
	            	c.setCooldownOne(one - 1);
	        	}
	        	if (two > 0) {
	            	c.setCooldownTwo(two - 1);
	        	}
	        	if (three > 0) {
	            	c.setCooldownThree(three - 1);
	        	}
	        	if (four > 0) {
	            	c.setCooldownFour(four - 1);
	        	}
	        }
        } else {
	        for (CharacterInstance c: bPost.getPlayerTwoTeam()) {
	        	int one = c.getCooldownOne();
	        	int two = c.getCooldownTwo();
	        	int three = c.getCooldownThree();
	        	int four = c.getCooldownFour();
	        	if (one > 0) {
	            	c.setCooldownOne(one - 1);
	        	}
	        	if (two > 0) {
	            	c.setCooldownTwo(two - 1);
	        	}
	        	if (three > 0) {
	            	c.setCooldownThree(three - 1);
	        	}
	        	if (four > 0) {
	            	c.setCooldownFour(four - 1);
	        	}
	        }
        }
        

        for (CharacterInstance c: bPost.getPlayerOneTeam()) {
        	List<Effect> oldList = c.getEffects();
            List<Effect> newList = new ArrayList<>();
        	if (oldList.size() > 0) {
        		for (Effect e: oldList) {
        			if (
    					(e.getOriginCharacter() < 3 && isPlayerOne) ||
    					(e.getOriginCharacter() > 2 && !isPlayerOne)
        					) {
	        			int dur = e.getDuration();
	        			if (e.getDuration() != 0) {
	        				if (e.getDuration() < 0) {
	        					if (e.getStatMods().get(Stat.SHIELDS) != null) {
	    	            			newList.add(e);
	        					} else {
	        						// dont add back a dead shields effect
	        					}
	        				} else {
			        			e.setDuration(dur - 1);
		            			newList.add(e);
	        				}
	        			}
        			} else {
        				if (e.getDuration() < 0) {
        					if (e.getStatMods().get(Stat.SHIELDS) != null) {
    	            			newList.add(e);
        					} else {
        						// dont add back a dead shields effect
        					}
        				} else if (e.getDuration() == 0) {
        					// TODO: this might break everything
        					// OR IT FIXED EVERYTHING??
        				} else {
	            			newList.add(e);
        				}
        			}
        			
        		}
        	}
        	c.setEffects(newList);
        }
        
        for (CharacterInstance c: bPost.getPlayerTwoTeam()) {
        	List<Effect> oldList = c.getEffects();
            List<Effect> newList = new ArrayList<>();
        	if (oldList.size() > 0) {
        		for (Effect e: oldList) {
        			if (
    					(e.getOriginCharacter() < 3 && isPlayerOne) ||
    					(e.getOriginCharacter() > 2 && !isPlayerOne)
        					) {
	        			int dur = e.getDuration();
	        			if (e.getDuration() != 0) {
	        				if (e.getDuration() < 0) {
	        					if (e.getStatMods().get(Stat.SHIELDS) != null) {
	    	            			newList.add(e);
	        					} else {
	        						// dont add back a dead shields effect
	        					}
	        				} else {
			        			e.setDuration(dur - 1);
		            			newList.add(e);
	        				}
	        			}
        			} else {
        				if (e.getDuration() < 0) {
        					if (e.getStatMods().get(Stat.SHIELDS) != null) {
    	            			newList.add(e);
        					} else {
        						// dont add back a dead shields effect
        					}
        				} else if (e.getDuration() == 0) {
        					// TODO: this might break everything
        					// OR IT FIXED EVERYTHING??
        				} else {
	            			newList.add(e);
        				}
        			}
        		}
        	}
        	c.setEffects(newList);
        }
        
        
        // for each ability/effect, in the correct order, resolve the effects.
        // basically logic and rules for all the stats qualities and conditions

        //countdown durations of current effects that have origin character > 2
        
        // consider hidden abilites here later >>

        // also pass back a map of (position, list<effect>) so the frontend can put them in their proper spots easier
        // then frontend can just take the proper positions to display on the turnEffectsMap
        
        // deal out more energy at the end
        if (bPost.getTurn() != 1) {
        	int count = 0;
	        if (playerId == b.getPlayerIdOne()) {
	        	for (CharacterInstance c : bPost.getPlayerTwoTeam()) {
	        		if (!c.isDead()) {
	        			count++;
	        		}
	        	}
	        	bPost.drawPlayerTwoEnergy(count);
	        } else {
	        	for (CharacterInstance c : bPost.getPlayerOneTeam()) {
	        		if (!c.isDead()) {
	        			count++;
	        		}
	        	}
	        	bPost.drawPlayerOneEnergy(count);
	        }
	        count = 0;
        }
        

        bPost = battleService.save(bPost);
        LOG.info(bPost.toString());
        String responseJson = "{\"type\": \"END\", \"playerId\": " + playerId + ", \"battle\": " + new Gson().toJson(bPost) + "}";
        return responseJson;
    }

    public String handleTargetCheckMessage(Map valueMap) {
        LOG.info("Target Check");
        Integer playerId = getMapEntryAsInt("playerId", valueMap);

        AbilityTargetDTO dto = mapper.convertValue(valueMap.get("abilityTargetDTO"), AbilityTargetDTO.class);

        int abPos = dto.getAbilityPosition();
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
        
        Battle b = battleService.getByPlayerId(playerId);
        boolean isPlayerOne = b.getPlayerIdOne() == playerId;
        List<CharacterInstance> team;
        List<CharacterInstance> enemyTeam;
        if (isPlayerOne) {
        	team = b.getPlayerOneTeam();
        	enemyTeam = b.getPlayerTwoTeam();
        	teamPos = charPos;
        } else {
        	team = b.getPlayerTwoTeam();
        	enemyTeam = b.getPlayerOneTeam();
        	teamPos = charPos - 3;
        }
//        List<Character> c = characterService.getCharactersForBattle(b);
        Ability a = dto.getAbility();
        if(a.isAoe()) {
            if(a.isEnemy()) {
            	for (CharacterInstance ch : enemyTeam) {
            		boolean canBe = this.canBeTargeted(ch.getEffects(), a.getAoeEnemyEffects());
            		if (canBe) {
            			tarPos.set(ch.getPosition(), ch.getPosition());
            		}
                	// write a method that takes in a list of effects on a character instance, also passes in ability to check for piercing and shit
//                    	and checks for anything that would prevent targeting, and if so, sets character position in array to -1
            	}
            }
            if (a.isAlly()) {
            	// do whole team here, null it out later in self if not the case
            	for (CharacterInstance ch : team) {
        			tarPos.set(ch.getPosition(), ch.getPosition());
            	}
            }
            if (!a.isSelf()) {
            	tarPos.set(charPos, -1);
            	// just null out location at parentIndex or whatever from the dto because this is AOE and covered above in ally
            }
        } else {
        	// SINGLE TARGET
            if(a.isEnemy()) {
            	for (CharacterInstance ch : enemyTeam) {
            		boolean canBe = this.canBeTargeted(ch.getEffects(), a.getEnemyEffects());
            		if (canBe) {
            			tarPos.set(ch.getPosition(), ch.getPosition());
            		}
                	// write a method that takes in a list of effects on a character instance, also passes in ability to check for piercing and shit
//                    	and checks for anything that would prevent targeting, and if so, sets character position in array to -1
            	}
            }
            if (a.isAlly()) {
            	// do whole team here
            	for (CharacterInstance ch : team) {
        			tarPos.set(ch.getPosition(), ch.getPosition());
            	}
            	// remove self from ally list
            	tarPos.set(charPos, -1);
            }
            if (a.isSelf()) {
            	CharacterInstance ch = team.get(teamPos);
//        		boolean canBe = this.canBeTargeted(ch.getEffects(), a.getSelfEffects());
    			tarPos.set(ch.getPosition(), ch.getPosition());
            	// just null out location at parentIndex or whatever from the dto
            }
        }


        
        //TODO: V build the object and throw it in here, front end should be good

        dto.setTargetPositions(tarPos);
        
        // (dont do this: deprecated) set character instances as highlighted if they can be targeted (no need to save i guess?)
        
        String responseJson = "{\"type\": \"TCHECK\", \"playerId\": " + playerId + ", \"dto\": " + new Gson().toJson(dto) + "}";
        return responseJson;
    }

    public String handleCostCheckMessage(Map valueMap) throws Exception {
        // respond with an array of the abilities that CAN be cast
        // currently responding with all
        Integer[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            	
    	
        LOG.info("Cost Check");
        Integer playerId = getMapEntryAsInt("playerId", valueMap);

        CostCheckDTO dto = mapper.convertValue(valueMap.get("costCheckDTO"), CostCheckDTO.class);
        
        List<List<String>> allAbilitiesCosts = dto.getAllyCosts();
        List<AbilityTargetDTO> chosenAbilities = dto.getChosenAbilities();

        // backend should know proper energy, just update the battle and save after each call.
        
        Battle b = battleService.getByPlayerId(playerId);
        List<Character> characters = characterService.getCharactersForBattle(b);

        Map<String, Integer> spentEnergy = new HashMap<>();
        Map<String, Integer> currentEnergy;
        List<CharacterInstance> units;
        List<Character> characterUnits;
        
        if (playerId == b.getPlayerIdOne()) {
        	units = b.getPlayerOneTeam();
        	currentEnergy = b.getPlayerOneEnergy();
        	characterUnits = characters.subList(0, 3);
        } else {
        	units = b.getPlayerTwoTeam();
        	currentEnergy = b.getPlayerTwoEnergy();
        	characterUnits = characters.subList(3, 6);
        }
        currentEnergy.put(Energy.RANDOM, 0);
        spentEnergy.put(Energy.RANDOM, 0);
		spentEnergy.put(Energy.STRENGTH, 0);
		spentEnergy.put(Energy.DEXTERITY, 0);
		spentEnergy.put(Energy.ARCANA, 0);
		spentEnergy.put(Energy.DIVINITY, 0);
        Integer[] charPos = {0, 1, 2};
                
        if (chosenAbilities != null) {
	        for (AbilityTargetDTO abilityTargetDto : chosenAbilities) {
	        	Ability ability = abilityTargetDto.getAbility();
	        	
	        	// this just sets up charPos for later
	        	charPos[abilityTargetDto.getCharacterPosition()] = -1;
	        	for (String string : ability.getCost()) {
	        		if (string.equals(Energy.RANDOM)) {
        				spentEnergy.put(Energy.RANDOM, spentEnergy.get(Energy.RANDOM) + 1);
	        		} else if (string.equals(Energy.STRENGTH)) {
        				spentEnergy.put(Energy.STRENGTH, spentEnergy.get(Energy.STRENGTH) + 1);
	        		} else if (string.equals(Energy.DEXTERITY)) {
        				spentEnergy.put(Energy.DEXTERITY, spentEnergy.get(Energy.DEXTERITY) + 1);
	        		} else if (string.equals(Energy.ARCANA)) {
        				spentEnergy.put(Energy.ARCANA, spentEnergy.get(Energy.ARCANA) + 1);
	        		} else if (string.equals(Energy.DIVINITY)) {
        				spentEnergy.put(Energy.DIVINITY, spentEnergy.get(Energy.DIVINITY) + 1);
	        		}
	        	}
	        }
        }

        Integer[] ab = this.checkIfStunned(a, characterUnits, units);
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
       	
        
        String responseJson = "{\"type\": \"CCHECK\", \"playerId\": " + playerId + ", \"usable\": " + new Gson().toJson(e) + "}";
        LOG.info(responseJson.toString());
        return responseJson;
    }
    
    public Integer[] checkIfStunned(Integer[] input, List<Character> team, List<CharacterInstance> instances) {
    	for(int i = 0; i < 3 ; i++) {
    		Character character = team.get(i);
    		CharacterInstance instance = instances.get(i);
    		boolean stunned = false;
    		boolean physStunned = false;
    		boolean magStunned = false;
    		
    		for (Effect e : instance.getEffects()) {
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
	        		if (character.getSlot1().isPhysical()) {
	        			input[0 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot2().isPhysical()) {
	        			input[1 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot3().isPhysical()) {
	        			input[2 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot4().isPhysical()) {
	        			input[3 + (4 * i)] = -1;
	        		}
	    		}
	    		if (magStunned) {
	        		if (character.getSlot1().isMagical()) {
	        			input[0 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot2().isMagical()) {
	        			input[1 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot3().isMagical()) {
	        			input[2 + (4 * i)] = -1;
	        		}
	        		if (character.getSlot4().isMagical()) {
	        			input[3 + (4 * i)] = -1;
	        		}
	    		}
    		}


    	
    	}
    	return input;
    }
    
    public Integer[] checkIfCharacterIsDead(Integer[] input, List<CharacterInstance> team) {
    	boolean characterOneIsDead = team.get(0).isDead();
    	boolean characterTwoIsDead = team.get(1).isDead();
    	boolean characterThreeIsDead = team.get(2).isDead();
    	
    	for (int i = 0; i < input.length ; i++) {
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
    	for(int i = 0; i < 3 ; i++) {
    		if (position[i] == -1) {
    			input[0+(i*4)] = -1;
    			input[1+(i*4)] = -1;
    			input[2+(i*4)] = -1;
    			input[3+(i*4)] = -1;
    		}
    	}
    	return input;
    }
    
    public Integer[] checkCooldowns(List<CharacterInstance> units, Integer[] input) {
        int counter = 0;

    	for (CharacterInstance ch : units) {
    		if (ch.getCooldownOne() > 0) {
    			input[counter] = -1;
    		}
        	counter++;
    		if (ch.getCooldownTwo() > 0) {
    			input[counter] = -1;
    		}
        	counter++;
    		if (ch.getCooldownThree() > 0) {
    			input[counter] = -1;
    		}
        	counter++;
    		if (ch.getCooldownFour() > 0) {
    			input[counter] = -1;
    		}
        	counter++;
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
    
    public boolean canBeTargeted(List<Effect> existingEffects, List<Effect> targetingEffects) {
    	boolean canBe = true;
    	// check for invulnerable, untargetable, and conditionals
    	
    	// conditional.hasquality is the condition but we probably don't need to check too many conditionals for targets, more like qualities.
    	for (Effect effect : existingEffects) {
    		// if enemy is invulnerable
    		if (Quality.INVULNERABLE.equals(effect.getQuality())) {
    			// check for invuln piercing
    			for (Effect targetingEffect : targetingEffects) {
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
    			for (Effect existingEffect : existingEffects) {
    				if (Quality.VULNERABLE.equals(existingEffect.getQuality())) {
    					canBe = true;
    					break;
    				}
    			}
    		}
    		if (Quality.UNTARGETABLE.equals(effect.getQuality())) {
    			// check for untargetbale piercing
    			for (Effect targetingEffect : targetingEffects) {
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
    			// check for targetable which cancels
    			for (Effect existingEffect : existingEffects) {
    				if (Quality.TARGETABLE.equals(existingEffect.getQuality())) {
    					canBe = true;
    					break;
    				}
    			}
    		}
    	}
    	// set up canBe based on the list of effects and the ability
    	
    	return canBe;
    }
}
