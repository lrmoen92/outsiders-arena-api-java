package org.outsiders.arena.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
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
  
  public Battle handleTurns(Battle battle, BattleTurnDTO dto) {
	  LOG.info("BATTLE:" + battle.toString());
	  
	  // abilities, targets, origin character
	  List<AbilityTargetDTO> moves = dto.getAbilities();
	  
	  // all energy spent (mostly to verify randoms) 
	  Map<String, Integer> spentEnergy = dto.getSpentEnergy();
	  
	  // order to resolve effects in (action bar)
	  List<String> effectIds = dto.getEffectIds();
	  
	  moves.forEach(move -> {
		  move.getAbility();
		  move.getTargets();
	  });
	  
	  return battle;
  }
  
  public int randomInt()
  {
    return new Random().nextInt();
  }
}
