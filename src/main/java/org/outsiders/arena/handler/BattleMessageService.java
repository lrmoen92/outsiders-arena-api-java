package org.outsiders.arena.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.CharacterInstance;
import org.outsiders.arena.domain.Energy;
import org.outsiders.arena.domain.Player;
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
    
    public String handleTurnEndMessage(Map valueMap) {
        LOG.info("Turn End");
        Integer playerId = getMapEntryAsInt("playerId", valueMap);
        Battle b = battleService.getByPlayerId(playerId);

        BattleTurnDTO dto = mapper.convertValue(valueMap.get("battleTurnDTO"), BattleTurnDTO.class);
        
        //first time this happens is the record of the "end" of turn 1
        b.setTurn(b.getTurn() + 1);
        // do battle logic here
        Battle bPost;
        if (playerId == b.getPlayerIdOne()) {
            bPost = nrg.handleTurns(b, dto, true);
        } else {
            bPost = nrg.handleTurns(b, dto, false);
        }
        
        // for each ability/effect, in the correct order, resolve the effects.
        
        // basically logic and rules for all the stats and qualities
        
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
        LOG.info(b.toString());
        String responseJson = "{\"type\": \"END\", \"playerId\": " + playerId + ", \"battle\": " + new Gson().toJson(b) + "}";
        return responseJson;
    }

    public String handleTargetCheckMessage(Map valueMap) {
        LOG.info("Target Check");
        int i = Integer.parseInt(valueMap.get("playerId").toString());

        int p = getMapEntryAsInt("ability", valueMap);
        Battle b = battleService.getByPlayerId(i);
        List<Character> c = characterService.getCharactersForBattle(b);
        // use i, p, and b to determine actual targets
        
        // set character instances as highlighted if they can be targeted (no need to save i guess?)
        
        String responseJson = "{\"type\": \"TCHECK\", \"playerId\": " + i + ", \"battle\": " + new Gson().toJson(b) + "}";
        return responseJson;
    }

    public String handleCostCheckMessage(Map valueMap) {
        // respond with an array of the abilities that CAN be cast
        // currently responding with all
        Integer[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        
        // this should also check cooldowns huh.
    	
    	
        LOG.info("Cost Check");
        int playerId = Integer.parseInt(valueMap.get("playerId").toString());
        // backend should know proper energy, just update the battle and save after each call.
        
        Map<String, Integer> energyMap = new HashMap<>();
        energyMap.put("RANDOM", 0);
        Battle b = battleService.getByPlayerId(playerId);
        List<Character> c = characterService.getCharactersForBattle(b);
        List<Ability> la = new ArrayList<>();
        
        if (playerId == b.getPlayerIdOne()) {
        	c = c.subList(0, 3);
        } else {
        	c = c.subList(3, 6);
        }
        Map<String, Integer> holder = energyMap;
        
        int counter = 0;
        for (Character ch : c) {
        	
        	List<String> cost1 = ch.getSlot1().getCost();
        	for (String string : cost1) {
        		if (string.equals(Energy.RANDOM)) {
        			
        		}
        		int newValue = energyMap.get(string) - 1;
        		if (newValue < 0) {
        			// -1 will be ignored
        			a[counter] = -1;
        		} else {
        			energyMap.put(string, newValue);
        		}
        	}        	
        	counter++;
        	energyMap = holder;
        	
        	List<String> cost2 = ch.getSlot2().getCost();
        	for (String string : cost2) {
        		if (energyMap.get(string) - 1 < 0) {
        			// -1 will be ignored
        			a[counter] = -1;
        		}
        	}        	
        	counter++;
        	
        	List<String> cost3 = ch.getSlot3().getCost();
        	for (String string : cost3) {
        		if (energyMap.get(string) - 1 < 0) {
        			// -1 will be ignored
        			a[counter] = -1;
        		}
        	}        	
        	counter++;
        	
        	List<String> cost4 = ch.getSlot4().getCost();
        	for (String string : cost4) {
        		if (energyMap.get(string) - 1 < 0) {
        			// -1 will be ignored
        			a[counter] = -1;
        		}
        	}        	
        	counter++;
        }
        
        String responseJson = "{\"type\": \"CCHECK\", \"playerId\": " + playerId + ", \"usable\": " + new Gson().toJson(a) + "}";
        return responseJson;
    }
}
