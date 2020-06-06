package org.outsiders.arena.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.CharacterInstance;
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
    private static Logger LOG = LoggerFactory.getLogger(BattleMessageService.class);
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
        if (!opponentName.isEmpty()) {
        	Battle battle = this.battleService.getByPlayerDisplayName(opponentName);
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
                battle.drawPlayerTwoEnergy(3);
            } else {
                battle.drawPlayerTwoEnergy(1);
            }

            ArrayList<Character> characters = new ArrayList<Character>();
            List<Integer> characterIds = battle.getPlayerOneTeam().stream().map(CharacterInstance::getCharacterId).collect(Collectors.toList());
            for (Integer id : characterIds) {
                characters.add(this.characterService.findById(id).get());
            }
        	List<Integer> characterIds2 = battle.getPlayerTwoTeam().stream().map(CharacterInstance::getCharacterId).collect(Collectors.toList());
            for (Integer id : characterIds2) {
                characters.add(this.characterService.findById(id).get());
            }
            battle.setBattleCharacters(characters);
            savedBattle = this.battleService.save(battle);
            
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
        } else {
	        Battle battle = new Battle();
	        battle.setId(this.nrg.randomInt());
	        battle.setArenaId(arenaId);
	        battle.setPlayerIdOne(playerId.intValue());
	        battle = this.battleService.save(battle);
	        ArrayList<CharacterInstance> list1 = new ArrayList<CharacterInstance>();
	        CharacterInstance i1 = new CharacterInstance();
	        CharacterInstance i2 = new CharacterInstance();
	        CharacterInstance i3 = new CharacterInstance();
	        i1.setCharacterId(characterId1.intValue());
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
            if (battle.isPlayerOneStart()) {
                battle.drawPlayerOneEnergy(1);
            } else {
                battle.drawPlayerOneEnergy(3);
            }
	        savedBattle = this.battleService.save(battle);
	        LOG.info("SAVED BATTLE:: " + savedBattle.toString());
	        return new Gson().toJson("WAITING FOR OPPONENTS");
        }
    }

    public String handleEnergyTradeMessage(Map valueMap) {
        LOG.info("Energy Trade");
        int i = getMapEntryAsInt("playerId", valueMap);
        Map m = (Map) valueMap.get("spent");
        String s = valueMap.get("chosen").toString();
        Battle b = battleService.getByPlayerId(i);
        
        b = battleService.save(b);
        String responseJson = "{\"type\": \"ETRADE\", \"playerId\": " + i + ", \"battle\": " + new Gson().toJson(b) + "}";
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
        
        Battle bPost = nrg.handleTurns(b, dto);
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
        // use i, p, and b to determine actual targets
        
        // set character instances as highlighted if they can be targeted (no need to save i guess?)
        
        String responseJson = "{\"type\": \"TCHECK\", \"playerId\": " + i + ", \"battle\": " + new Gson().toJson(b) + "}";
        return responseJson;
    }

    public String handleCostCheckMessage(Map valueMap) {
        LOG.info("Cost Check");
        int playerId = Integer.parseInt(valueMap.get("playerId").toString());
        Battle b = battleService.getByPlayerId(playerId);
        
        if (playerId == b.getPlayerIdOne()) {
        	for (CharacterInstance c : b.getPlayerOneTeam()) {
        		b.getBattleCharacters();
        		
        	}
        } else {
        	for (CharacterInstance c : b.getPlayerTwoTeam()) {

        	}
        }
        
        
        // respond with an array of the abilities that CAN be cast
        // currently responding with all
        Integer[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        
        String responseJson = "{\"type\": \"CCHECK\", \"playerId\": " + playerId + ", \"usable\": " + new Gson().toJson(a) + "}";
        return responseJson;
    }
}
