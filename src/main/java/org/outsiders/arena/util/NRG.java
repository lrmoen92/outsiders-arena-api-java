package org.outsiders.arena.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.Effect;
import org.outsiders.arena.domain.Energy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NRG
{
	private static final Logger LOG = LoggerFactory.getLogger(NRG.class);
  public List<String> draw(int i)
  {
    List<String> res = new ArrayList();
    for (int j = i; j > 0; j--)
    {
      Random random = new Random();
      random.nextInt(4);
      int det = random.nextInt() % 4;
      if (det < 0) {
    	  det = 0 - det;
      }
      res.add(det == 2 ? "STRENGTH" : det == 1 ? "DIVINITY" : det == 0 ? "ARCANA" : "DEXTERITY");
    }
    return res;
  }
  
  public Map<String, Integer> drawEnergy(int i) {
	  return drawEnergy(i, null);
  }
  
  public Map<String, Integer> drawEnergy(int i, Map<String, Integer> previous) {
	  if (previous == null) {
		  previous = new HashMap<>();
		  previous.put(Energy.STRENGTH, 0);
		  previous.put(Energy.DEXTERITY, 0);
		  previous.put(Energy.ARCANA, 0);
		  previous.put(Energy.DIVINITY, 0);
	  }
	  
	  List<String> drawnEnergy = draw(i);
	  for (String energy : drawnEnergy) {
		  Integer oldVal = previous.get(energy);
		  previous.put(energy, oldVal + 1);
	  }
	  
	  return previous;
  }
  
  public Battle handleTurns(Battle battle, BattleTurnDTO dto, boolean isPlayerOne) {
	  LOG.info("BATTLE:" + battle.toString());
	   
	  // all energy spent (mostly to verify randoms) 
	  Map<String, Integer> spentEnergy = dto.getSpentEnergy();
	  // update player's energy in battle
	  
	  if (isPlayerOne) {
		  spentEnergy.forEach((s,i) -> {
			 int prev = battle.getPlayerOneEnergy().get(s);
			 battle.getPlayerOneEnergy().put(s, prev - i);
		  });
	  } else {
		  spentEnergy.forEach((s,i) -> {
			 int prev = battle.getPlayerTwoEnergy().get(s);
			 battle.getPlayerTwoEnergy().put(s, prev - i);
		  });
	  }
	  
	  // abilities, targets, origin character
	  List<AbilityTargetDTO> moves = dto.getAbilities();
	  // 3 ability target DTOs in the proper order
	  
	  // order to resolve effects in (action bar)
	  // we are probably assigning these here (ints) (negative numbers are new abilities)
	  List<Effect> effectIds = dto.getEffects();
	  // resolve all of these in order, if negative pull next abilityTargetDTO
	  
	  
	  
	  return battle;
  }
  
  public int randomInt()
  {
    return new Random().nextInt();
  }
}
