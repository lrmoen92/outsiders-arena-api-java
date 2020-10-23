package org.outsiders.arena.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Player;
import org.outsiders.arena.domain.PlayerMessage;
import org.outsiders.arena.service.CharacterService;
import org.outsiders.arena.service.PlayerService;
import org.outsiders.arena.util.SortById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController
{
	  private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);
	
  @Autowired
  private PlayerService playerService;
  
  @Autowired
  private CharacterService characterService;
  
  // player name - arena id
  private Map<String, Integer> privateMatchMaking = new HashMap<>();
  
  // player id - level
  private Map<Integer, Integer> publicMatchMaking = new HashMap<>();
  
  // player id - level
  private Map<Integer, Integer> ladderMatchMaking = new HashMap<>();
  
  // player id - arena id
  private Map<Integer, Integer> stagedGames = new HashMap<>();
  

  @RequestMapping(value= {"/api/player/arena/ladder/{playerId}/{playerLevel}"}, method=RequestMethod.GET)
  public int getLadderMatchArena(@PathVariable int playerId, @PathVariable int playerLevel) throws Exception {	  
	  boolean waiting = true;
	  int found = 0;
	  int seconds = 0;
	  int output = 0;
	  addToLadderMatchMaking(playerId, playerLevel);
	  while (waiting) {	  
		  if (seconds > 30) {
			  // if someone doesn't connect in 30 seconds, reply with "empty queue" (0) or throw http exception
			  output = 0;
			  // and remove myself from queue
			  removeFromLadderMatchMaking(playerId);
			  waiting = false;
		  }
		  
		  // wait 1 second
		  Thread.sleep(1000);
		  seconds++;
		  LOG.info(seconds + " seconds have passed");
		  
	      // check map of staged games for my ID
	      if (existsInStagedGames(playerId)) {
		      // get arena id from it and clean up
	    	  output = getArenaIdFromStagedGamesForPlayerId(playerId);
	    	  removeFromStagedGames(playerId);
	    	  waiting = false;
	      }
	      if (waiting) {
			  // we're taking awhile
			  if (seconds > 25) {
				  // grab anyone we can
				  found = checkForAnyLadderMatchMaking(playerId, playerLevel);
			  } else if (seconds > 15) {
				  // look for less eligible player
				  found = checkForEligibleLadderMatchMaking(playerId, playerLevel, 15);
			  } else {
				  // look for eligible player
				  found = checkForEligibleLadderMatchMaking(playerId, playerLevel, 5);
			  }
	      }

		  // found someone
		  if (found != 0) {
			  // put found with a new random arena id
    	      output = ThreadLocalRandom.current().nextInt(100000, 999999);
	    	  addToStagedGames(found, output);
	    	  
			  // remove both from MatchMaking (its weird but this works better if you think about it)
		      removeFromLadderMatchMaking(found);
		      removeFromLadderMatchMaking(playerId);
		      waiting = false;
		  }

	  }	
	  return output;
  }

  @RequestMapping(value= {"/api/player/arena/quick/{playerId}/{playerLevel}"}, method=RequestMethod.GET)
  public int getQuickMatchArena(@PathVariable int playerId, @PathVariable int playerLevel) throws Exception {	  
	  boolean waiting = true;
	  int found = 0;
	  int seconds = 0;
	  int output = 0;
	  addToPublicMatchMaking(playerId, playerLevel);
	  while (waiting) {	  
		  if (seconds > 30) {
			  // if someone doesn't connect in 30 seconds, reply with "empty queue" (0) or throw http exception
			  output = 0;
			  // and remove myself from queue
			  removeFromPublicMatchMaking(playerId);
			  waiting = false;
		  }
		  
		  // wait 1 second
		  Thread.sleep(1000);
		  seconds++;
		  LOG.info(seconds + " seconds have passed");
		  
	      // check map of staged games for my ID
	      if (existsInStagedGames(playerId)) {
		      // get arena id from it and clean up
	    	  output = getArenaIdFromStagedGamesForPlayerId(playerId);
	    	  removeFromStagedGames(playerId);
	    	  waiting = false;
	      }
	      if (waiting) {
			  // we're taking awhile
			  if (seconds > 25) {
				  // grab anyone we can
				  found = checkForAnyPublicMatchMaking(playerId, playerLevel);
			  } else if (seconds > 15) {
				  // look for less eligible player
				  found = checkForEligiblePublicMatchMaking(playerId, playerLevel, 15);
			  } else {
				  // look for eligible player
				  found = checkForEligiblePublicMatchMaking(playerId, playerLevel, 5);
			  }
	      }

		  // found someone
		  if (found != 0) {
			  // put found with a new random arena id
    	      output = ThreadLocalRandom.current().nextInt(100000, 999999);
	    	  addToStagedGames(found, output);
	    	  
			  // remove both from MatchMaking (its weird but this works better if you think about it)
		      removeFromPublicMatchMaking(found);
		      removeFromPublicMatchMaking(playerId);
		      waiting = false;
		  }

	  }	
	  return output;
  }
  
  @RequestMapping(value={"/api/player/"}, method=RequestMethod.POST)
  public Player getOrCreatePlayer(@RequestBody PlayerMessage message)
  {
    	Player player = this.playerService.findByDisplayName(message.getDisplayName());
    	if (player != null) {
    		LOG.info(player.getDisplayName() + " logged in");
    	      return player;
    	} else {
    		player = new Player();
    	      player.setDisplayName(message.getDisplayName());
    	      player.setAvatarUrl(message.getAvatarUrl());
    	      int randomNum = ThreadLocalRandom.current().nextInt(0, 100000000);
    	      player.setId(randomNum);
    	      player.setCharacterIdsUnlocked(Arrays.asList(1, 2, 3, 4, 5));
    	      player = this.playerService.save(player);
    	      LOG.info("Created new Player: " + player.toString());
    	}

      return player;

  }
  
  @RequestMapping(value={"/api/player/arena/{playerId}/{opponentName}"}, method=RequestMethod.GET)
  public int getArenaForPlayer(@PathVariable int playerId, @PathVariable String opponentName) {
	  if (privateMatchMaking.get(opponentName) != null) {
		  int arenaId = privateMatchMaking.get(opponentName);
		  privateMatchMaking.remove(opponentName);
		  LOG.info("Player ID: (" + playerId + ") found opponent named " + opponentName + " with Arena ID: (" + arenaId + ")");
		  return arenaId;
	  } else {
	      int randomNum = ThreadLocalRandom.current().nextInt(10000000, 99999999);
		  privateMatchMaking.put(playerService.findById(playerId).get().getDisplayName(), randomNum);
		  LOG.info("Player ID: (" + playerId + ") could not find opponent named " + opponentName + ".  Waiting with Arena ID: (" + randomNum + ")");
		  return randomNum;
	  }
  }
  
  @RequestMapping(value={"/api/character/"}, method=RequestMethod.GET)
  public List<Character> getAllCharacters() {
	  List<Character> list = new ArrayList<Character>();
	  characterService.findAll().forEach(list::add);
	  list.sort(new SortById());
	  return list;
  }
  

	public synchronized boolean existsInStagedGames(Integer in) {
		return this.stagedGames.containsKey(in);
	}
  
	public synchronized int getArenaIdFromStagedGamesForPlayerId(Integer in) {
		return this.stagedGames.get(in);
	}
	
	public synchronized void addToStagedGames(Integer in, Integer in2) {
		this.stagedGames.put(in, in2);
	}
	
	public synchronized void removeFromStagedGames(Integer in) {
		this.stagedGames.remove(in);
	}

	
	
	public synchronized int checkForAnyLadderMatchMaking(Integer id, Integer lvlIn) {
		int output = 0;
		
		for(Map.Entry<Integer, Integer> entry : getLadderMatchMakingEntries()) {
		    if (entry.getKey() != id) {
		       output = entry.getKey();
		       break;
		    }
		}
		
		return output;
	}

	public synchronized int checkForEligibleLadderMatchMaking(Integer id, Integer lvlIn, Integer range) {
		int output = 0;
		
		for(Map.Entry<Integer, Integer> entry : getLadderMatchMakingEntries()) {
		    if ( Math.abs(entry.getValue() - lvlIn) <= range && entry.getKey().intValue() != id.intValue()) {
		       output = entry.getKey();
		       break;
		    }
		}
		
		return output;
	}
	
	public synchronized Set<Map.Entry<Integer, Integer>> getLadderMatchMakingEntries() {
		return this.ladderMatchMaking.entrySet();
	}
	
	public synchronized void addToLadderMatchMaking(Integer in, Integer in2) {
		this.ladderMatchMaking.put(in, in2);
	}
	
	public synchronized void removeFromLadderMatchMaking(Integer in) {
		this.ladderMatchMaking.remove(in);
	}
	
	

	public synchronized int checkForAnyPublicMatchMaking(Integer id, Integer lvlIn) {
		int output = 0;
		
		for(Map.Entry<Integer, Integer> entry : getPublicMatchMakingEntries()) {
		    if (entry.getKey() != id) {
		       output = entry.getKey();
		       break;
		    }
		}
		
		return output;
	}

	public synchronized int checkForEligiblePublicMatchMaking(Integer id, Integer lvlIn, Integer range) {
		int output = 0;
		
		for(Map.Entry<Integer, Integer> entry : getPublicMatchMakingEntries()) {
		    if ( Math.abs(entry.getValue() - lvlIn) <= range && entry.getKey().intValue() != id.intValue()) {
		       output = entry.getKey();
		       break;
		    }
		}
		
		return output;
	}
	
	public synchronized Set<Map.Entry<Integer, Integer>> getPublicMatchMakingEntries() {
		return this.publicMatchMaking.entrySet();
	}
	
	public synchronized void addToPublicMatchMaking(Integer in, Integer in2) {
		this.publicMatchMaking.put(in, in2);
	}
	
	public synchronized void removeFromPublicMatchMaking(Integer in) {
		this.publicMatchMaking.remove(in);
	}
	
}
