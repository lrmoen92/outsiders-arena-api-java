package org.outsiders.arena.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.h2.util.StringUtils;
import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.CharacterInstance;
import org.outsiders.arena.domain.Effect;
import org.outsiders.arena.domain.Energy;
import org.outsiders.arena.domain.Quality;
import org.outsiders.arena.domain.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class NRG
{
	private static final Logger LOG = LoggerFactory.getLogger(NRG.class);
	
	  public void randomlyRemoveN(Map<String, Integer> energy, int n)
	  {
		  for (int i = n; i > 0; i--) {
			  randomlyRemoveOne(energy);
		  }
    }
	
	  public void randomlyRemoveOne(Map<String, Integer> energy)
	  {
		  List<String> options = new ArrayList<>();
		  for (Map.Entry<String, Integer> entry : energy.entrySet()) {
			  if (entry.getValue() != 0) {
				  options.add(entry.getKey());
			  }
		  }

	      if (options.size() > 0) {
	          Collections.shuffle(options);
	          String chosen = options.get(0);
	          int oldVal = energy.get(chosen);
	          energy.put(chosen, oldVal -1);
	      }

      }
	
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
  
  public Battle handleTurns(Battle battle, BattleTurnDTO dto, boolean isPlayerOne) throws Exception {
	  LOG.info("BATTLE:" + battle.toString());
	  List<CharacterInstance> team;
	  List<CharacterInstance> enemyTeam;
	  Map<String, Integer> energy;
	  Map<String, Integer> enemyEnergy;
	  if (isPlayerOne) {
		  team = battle.getPlayerOneTeam();
		  enemyTeam = battle.getPlayerTwoTeam();
		  energy = battle.getPlayerOneEnergy();
		  enemyEnergy = battle.getPlayerTwoEnergy();
	  } else {
		  team = battle.getPlayerTwoTeam();
		  enemyTeam = battle.getPlayerOneTeam();
		  energy = battle.getPlayerTwoEnergy();
		  enemyEnergy = battle.getPlayerOneEnergy();
	  }
	   
	  // all energy spent (mostly to verify randoms) 
	  Map<String, Integer> spentEnergy = dto.getSpentEnergy();
	  // update player's energy in battle
	  
	  // ACTUALLY SPENDS THE ENERGY
	  spentEnergy.forEach((s,i) -> {
		 int prev = energy.get(s);
		 energy.put(s, prev - i);
	  });

	  
	  // TODO: LOGAN WE'RE BASICALLY DOWN TO HERE
	  // abilities, targets, origin character
	  List<AbilityTargetDTO> moves = dto.getAbilities();
	  // 3 ability target DTOs in the proper order
	  
	  // extract all effects from abilities 
	  
	  // order to resolve effects in (action bar)
	  
	  // instanceID is negative for chosen abilities, for the cancel button
	  // these will have instance ID's if they're duration effects
	  List<Effect> effects = dto.getEffects();
	  // ^ these effects having things like TargetCharacter, OriginCharacter, InstanceID and the like, are important and therefore must be assigned when we set a new Effect
	  // THIS COMMENT HERE LOGAN VV
	  // resolve all of these effects in order, if -1 pull next abilityTargetDTO
	  int counter = 0;
	  for (Effect effect : effects) {
		  if (effect.getInstanceId() == -1) {
			  AbilityTargetDTO atDTO = moves.get(counter);
			  
			  // DTO logic
			  Ability a = atDTO.getAbility();
			  int charPos = atDTO.getCharacterPosition();
			  int teamPos = charPos > 2 ? charPos - 3 : charPos;
			  List<Integer> tarPos = atDTO.getTargetPositions();

			  CharacterInstance self = team.get(teamPos);
			  
			  // take character at these positions
			  // do checks to apply this abilities effects in order
			  // charPos is only used when setting up new effects so they show properly on UI
			  // setting teamPos just to pull proper characters easier
			  // THIS IS THE SPOT
			  
			  
			  // for all the targets, apply targeted effects,
			  // then check for any self effects afterward
			  // this should mutate the teams, which should auto reflect on the battle
			  this.applyAbilityToTargets(a, team, enemyTeam, enemyEnergy, energy, tarPos, self);
			  // THIS is where we need to set up Effects properly, not just transfer them over. assign id's and origins and targets.
			  
			  counter++;
		  } else {
			  int origin = effect.getOriginCharacter();
			  // this is broken for AOE V 
			  int target = effect.getTargetCharacter();
			  CharacterInstance targetCharacter;
			  CharacterInstance originCharacter;
			  if (origin > 2) {
				  originCharacter = battle.getPlayerTwoTeam().get(origin - 3);
			  } else {
				  originCharacter = battle.getPlayerOneTeam().get(origin);
			  }
			  if (target > 2) {
				  targetCharacter = battle.getPlayerTwoTeam().get(target - 3);
			  } else {
				  targetCharacter = battle.getPlayerOneTeam().get(target);
			  }
			  // use effect.getTargetPosition and getOriginPosition to find these characters
			  
			  this.applyEffectToCharacter(effect, targetCharacter, originCharacter, enemyEnergy, energy, effect.getInstanceId());
			  // this.applyEffectToCharacters ?? probably
			  // this part will come second... and I imagine be just a method or copy of what I write above
		  }
	  }
	  // BRAIN TIME BRO, LITERALLY EVERYTHING ELSE MOSTLY IS THERE OR NEEDS A BRAIN TO TEST
	  
	  
	  // TODO: for all active effects if duration is 0 remove it
	  
	  
	  
	  // set abilities Cooldowns after all is said and done
	  for (AbilityTargetDTO atDTO : moves) {
		  int cd = atDTO.getAbility().getCooldown();
		  int index2 = atDTO.getAbilityPosition();
		  int index;
		  if (index2 > 7) {
			  index = 2;
		  } else if (index2 > 3) {
			  index = 1;
		  } else {
			  index = 0;
		  }
		  index2++;
		  CharacterInstance cha = team.get(index);
		  if (index2 % 4 == 0) {
			  cha.setCooldownFour(cd);
		  } else if (index2 % 4 == 1) {
			  cha.setCooldownOne(cd);
		  } else if (index2 % 4 == 2) {
			  cha.setCooldownTwo(cd);
		  } else if (index2 % 4 == 3) {
			  cha.setCooldownThree(cd);
		  } else {
			  throw new Exception();
		  }
		  team.set(index, cha);
	  }
	  
	  return battle;
  }
  
  public void applyAbilityToTargets(Ability a, List<CharacterInstance> allies, List<CharacterInstance> enemies, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, List<Integer> targetPositions, CharacterInstance self) throws Exception {
	  int randomInt = this.randomInt() + 1;
	  if(a.isAoe()) {
		  if(a.isEnemy()) {
          	a.getAoeEnemyEffects().forEach(e -> {
          		this.applyEffectToCharacters(e, enemies, self, enemyEnergy, energy, targetPositions, randomInt);
          	});
          }
		  if (a.isAlly()) {
          	a.getAoeAllyEffects().forEach(e -> {
          		this.applyEffectToCharacters(e, allies, self, enemyEnergy, energy, targetPositions, randomInt);
          	});
          }
      } else {
      	// SINGLE TARGET

          if (a.isSelf()) {
        	a.getSelfEffects().forEach(e -> {
          		this.applyEffectToCharacter(e, self, self, enemyEnergy, energy, randomInt);
          	});
    	  }
          if(a.isEnemy()) {
          	a.getEnemyEffects().forEach(e -> {
          		this.applyEffectToCharacters(e, enemies, self, enemyEnergy, energy, targetPositions, randomInt);
          	});
          }
          if (a.isAlly()) {
          	a.getAllyEffects().forEach(e -> {
          		this.applyEffectToCharacters(e, allies, self, enemyEnergy, energy, targetPositions, randomInt);
          	});
          }

      }
	  
  };
  
  public void applyEffectToCharacters(Effect effect, List<CharacterInstance> characters, CharacterInstance fromCharacter, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, List<Integer> targetPositions, int randomInt) {

	  for(CharacterInstance c: characters) {
		  if (targetPositions.contains(c.getPosition())) {
			  this.applyEffectToCharacter(effect, c, fromCharacter, enemyEnergy, energy, randomInt);
		  }
	  }
	  
  };
  
  public void applyEffectToCharacter(Effect effect, CharacterInstance character, CharacterInstance fromCharacter, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, int randomInt) {
	  List<Effect> current = character.getEffects();
	  List<Effect> currentFrom = fromCharacter.getEffects();
	  boolean interruptable = false;
	  boolean hasMods = false;
	  boolean isEnergyChange = false;

	  boolean isRandomChange = false;
	  boolean isStrengthChange = false;
	  boolean isDexterityChange = false;
	  boolean isArcanaChange = false;
	  boolean isDivinityChange = false;
	  boolean isDmg = false;
	  boolean isHeal = false;
	  boolean isShield = false;
	  boolean isStunned = false;
	  boolean isPhysStunned = false;
	  boolean isMagStunned = false;
	  boolean isShieldGain = false;
	  boolean isAffliction = false;
	  boolean shieldsApplied = false;
	  boolean targetInvulnerable = false;
	  boolean targetVulnerable = false;
	  boolean isDamagable = true;
	  
	  int shields = 0;
	  int gainAmt = 0;
	  int totalIn = 0;
	  int totalAr = 0;
	  
	  for (Effect e : currentFrom) {
		  if (Quality.STUNNED.equals(e.getQuality())) {
			  isStunned = true;
		  }
		  if (Quality.PHYSICAL_STUNNED.equals(e.getQuality())) {
			  isPhysStunned = true;
		  }
		  if (Quality.MAGICAL_STUNNED.equals(e.getQuality())) {
			  isMagStunned = true;
		  }
	  }
	  for (Effect e : current) {
		  if (Quality.INVULNERABLE.equals(e.getQuality())) {
			  targetInvulnerable = true;
		  }
		  if (Quality.VULNERABLE.equals(e.getQuality())) {
			  targetVulnerable = true;
		  }
	  }

	  
	  boolean isConditional = effect.isConditional();
	  boolean conditional = false;
	  if (isConditional) {
		  // ACTUALLY CHECK Effect.getCondition() here, somehow
		  
//		  conditional = true;
	  }
	  boolean isInterruptable = effect.isInterruptable();
	  if (isInterruptable) {
		  if (isStunned) {
			  interruptable = true;
		  }
		  if(effect.isPhysical() && isPhysStunned) {
			  interruptable = true;
		  }
		  if (effect.isMagical() && isMagStunned) {
			  interruptable = true;
		  }
	  }
	  
	  // TODO: here for conditionals
	  // effect.getCondition();
	  // checking of conditionals
	  if (conditional) {
		  // just don't do anything
	  } else if (interruptable) {
		  
	  } else {
		  if (!CollectionUtils.isEmpty(effect.getStatMods())) {
			  boolean isDamage = effect.getStatMods().get(Stat.DAMAGE) != null;
			  isDmg = isDamage ||
					  effect.getStatMods().get(Stat.PIERCING_DAMAGE) != null ||
					  effect.getStatMods().get(Stat.BONUS_DAMAGE) != null ||
					  effect.getStatMods().get(Stat.TRUE_DAMAGE) != null;
			  isDamagable = !targetInvulnerable || (targetInvulnerable && targetVulnerable);
			  isHeal = isDamage ? effect.getStatMods().get(Stat.DAMAGE) < 0 : false;
			  isShield = effect.getStatMods().get(Stat.SHIELDS) != null;
			  isShieldGain = effect.getStatMods().get(Stat.SHIELD_GAIN) != null;
			  isAffliction = effect.isAffliction();
			  isRandomChange = effect.getStatMods().get(Stat.ENERGY_CHANGE) != null;
			  isStrengthChange = effect.getStatMods().get(Stat.STRENGTH_CHANGE) != null;
			  isDexterityChange = effect.getStatMods().get(Stat.DEXTERITY_CHANGE) != null;
			  isArcanaChange = effect.getStatMods().get(Stat.ARCANA_CHANGE) != null;
			  isDivinityChange = effect.getStatMods().get(Stat.DIVINITY_CHANGE) != null;
			  isEnergyChange = isRandomChange || isStrengthChange || isDexterityChange || isArcanaChange || isDivinityChange;
			  //TODO:
			  // CHECK INVULNERABLE HERE (for existing effects)
			  //check if character has resistance, mods, boosts, etc to this type of damage
			  for (Effect e : currentFrom) {
				  if (isDmg && !isHeal) {
					  if (e.getStatMods().get(Stat.DAMAGE_OUT) != null) {
						  totalIn = totalIn + e.getStatMods().get(Stat.DAMAGE_OUT);
						  hasMods = true;
					  }
					  if (e.getStatMods().get(Stat.PHYSICAL_DAMAGE_OUT) != null && effect.isPhysical()) {
						  totalIn = totalIn + e.getStatMods().get(Stat.PHYSICAL_DAMAGE_OUT);
						  hasMods = true;
					  }
					  if (e.getStatMods().get(Stat.MAGICAL_DAMAGE_OUT) != null && effect.isMagical()) {
						  totalIn = totalIn + e.getStatMods().get(Stat.MAGICAL_DAMAGE_OUT);
						  hasMods = true;
					  }
					  if (e.getStatMods().get(Stat.AFFLICTION_DAMAGE_OUT) != null && effect.isAffliction()) {
						  totalIn = totalIn + e.getStatMods().get(Stat.AFFLICTION_DAMAGE_OUT);
						  hasMods = true;
					  }
				  }
			  }
			  for (Effect e : current) {
				  if (isShieldGain && !shieldsApplied) {
					  gainAmt = effect.getStatMods().get(Stat.SHIELD_GAIN);
					  if (e.getName().equals(effect.getName())) {
						 if (e.getStatMods().get(Stat.SHIELDS) != null) {
							 shields = e.getStatMods().get(Stat.SHIELDS);
							 e.getStatMods().put(Stat.SHIELDS, shields + gainAmt);
							 e.setDescription("This unit has " + (shields + gainAmt) + " shields.");
							 shieldsApplied = true;
						 }
					  }
				  }
				  if (isDmg && !isHeal){
					  // TODO: check for DAMAGE_DOWN too
					  if (e.getStatMods().get(Stat.DAMAGE_IN) != null) {
						  totalIn = totalIn + e.getStatMods().get(Stat.DAMAGE_IN);
						  hasMods = true;
					  }
					  if (e.getStatMods().get(Stat.ARMOR) != null && !isAffliction) {
						  totalAr = totalAr + e.getStatMods().get(Stat.ARMOR);
						  hasMods = true;
					  }
					  if (effect.isMagical()) {
						  if (e.getStatMods().get(Stat.MAGICAL_DAMAGE_IN) != null) {
							  totalIn = totalIn + e.getStatMods().get(Stat.MAGICAL_DAMAGE_IN);
							  hasMods = true;
						  }
						  if (e.getStatMods().get(Stat.MAGICAL_ARMOR) != null) {
							  // TODO: gonna have to revisit this if magical/physical are in the
							  // OR MAYBE NOT??
							  totalAr = totalAr + e.getStatMods().get(Stat.MAGICAL_ARMOR);
							  hasMods = true;
						  }
					  }
					  if (effect.isPhysical()) {
						  if (e.getStatMods().get(Stat.PHYSICAL_DAMAGE_IN) != null) {
							  totalIn = totalIn + e.getStatMods().get(Stat.PHYSICAL_DAMAGE_IN);
							  hasMods = true;
						  }
						  if (e.getStatMods().get(Stat.PHYSICAL_ARMOR) != null) {
							  totalAr = totalAr + e.getStatMods().get(Stat.PHYSICAL_ARMOR);
							  hasMods = true;
						  }
					  }
					  if (isAffliction) {
						  if (e.getStatMods().get(Stat.AFFLICTION_DAMAGE_IN) != null) {
							  totalIn = totalIn + e.getStatMods().get(Stat.AFFLICTION_DAMAGE_IN);
							  hasMods = true;
						  }
					  }
				  } 
			  }
			  if (isDmg || isHeal) {
				  Integer finalDamage = 0;
				  if (effect.getStatMods().get(Stat.DAMAGE) != null) {
					  if (hasMods && !isHeal) {
						  int dmg = effect.getStatMods().get(Stat.DAMAGE);
						  int flatDmg = dmg + totalIn;
						  if (flatDmg > 0) {
							  finalDamage = Math.round(flatDmg * ((100 - totalAr) / 100));	  
						  }
					  } else {
						  finalDamage = effect.getStatMods().get(Stat.DAMAGE);
					  }
				  }
				  if (effect.getStatMods().get(Stat.BONUS_DAMAGE) != null) {
					  if (hasMods) {
						  int dmg = effect.getStatMods().get(Stat.BONUS_DAMAGE);
						  finalDamage = Math.round(dmg * ((100 - totalAr) / 100));
					  } else {
						  finalDamage = finalDamage + effect.getStatMods().get(Stat.BONUS_DAMAGE);
					  }
				  }
				  if (effect.getStatMods().get(Stat.PIERCING_DAMAGE) != null) {
					  if (hasMods) {
						  int dmg = effect.getStatMods().get(Stat.PIERCING_DAMAGE);
						  int flatDmg = dmg + totalIn;
						  if (flatDmg > 0) {
							  finalDamage = finalDamage + flatDmg;
						  }
					  } else {
						  finalDamage = finalDamage + effect.getStatMods().get(Stat.PIERCING_DAMAGE);
					  }
				  }
				  
				  // remove appropriate amount of shields
				  // if heal, or target is invuln, dont mess with shields
				  if (!isHeal && isDamagable) {
					  for(Effect ef : current) {
						  if (ef.getStatMods().get(Stat.SHIELDS) != null && !effect.isAffliction()) {
							  shields = ef.getStatMods().get(Stat.SHIELDS);
							  if (finalDamage > 0) {
								  finalDamage = finalDamage - shields;
								  if (finalDamage >= 0) {
									  ef.getStatMods().remove(Stat.SHIELDS);
								  } else if (finalDamage < 0) {
									  ef.getStatMods().put(Stat.SHIELDS, -finalDamage);
								      ef.setDescription("This unit has " + -finalDamage + " shields.");
								      finalDamage = 0;
								  }
							  }
						  }
					  } 
				  }
				  int trueDmg = 0;
				  // by putting this here, true damage trounces ALL
				  if (effect.getStatMods().get(Stat.TRUE_DAMAGE) != null) {
					  trueDmg = effect.getStatMods().get(Stat.TRUE_DAMAGE);
				  }

				  // actually set the final damage
				  int oldHP = character.getHp();
				  // if invuln dont do any of the damage from above
				  if (!isDamagable) {
					  character.setHp(oldHP - trueDmg);
				  } else {
					  character.setHp(oldHP - (finalDamage + trueDmg));
				  }

			  }
			  
			  // SHIELD AND ENERGY GAIN VV
			  if (isEnergyChange) {
				  Map<String, Integer> targeted;
				  if (Math.abs(character.getPosition() - fromCharacter.getPosition()) > 2) {
					  targeted = enemyEnergy;
				  } else {
					  targeted = energy;
				  }
					  
				  if (isStrengthChange) {
					  int val = effect.getStatMods().get(Stat.STRENGTH_CHANGE);
					  int oldVal = targeted.get(Energy.STRENGTH);
					  int newVal = oldVal + val;
					  if (newVal >= 0) {
						  targeted.put(Energy.STRENGTH, newVal);
					  }
				  }
				  if (isDexterityChange) {
					  int val = effect.getStatMods().get(Stat.DEXTERITY_CHANGE);
					  int oldVal = targeted.get(Energy.DEXTERITY);
					  int newVal = oldVal + val;
					  if (newVal >= 0) {
						  targeted.put(Energy.DEXTERITY, newVal);
					  }
				  }
				  if (isArcanaChange) {
					  int val = effect.getStatMods().get(Stat.ARCANA_CHANGE);
					  int oldVal = targeted.get(Energy.ARCANA);
					  int newVal = oldVal + val;
					  if (newVal >= 0) {
						  targeted.put(Energy.ARCANA, newVal);
					  }
				  }
				  if (isDivinityChange) {
					  int val = effect.getStatMods().get(Stat.DIVINITY_CHANGE);
					  int oldVal = targeted.get(Energy.DIVINITY);
					  int newVal = oldVal + val;
					  if (newVal >= 0) {
						  targeted.put(Energy.DIVINITY, newVal);
					  }
				  }
				  if (isRandomChange) {
					  int val = effect.getStatMods().get(Stat.ENERGY_CHANGE);
					  if (val > 0) {
						  drawEnergy(val, targeted);
					  } else if (val < 0) {
						  randomlyRemoveN(targeted, val);
					  }
				  }
			  }
			  
			  // check buffs on fromCharacter too
			  // adjust stats accordingly, check for resists, magical physical, here from "current"
			  // check for existence of Stat.DAMAGE, Stat.PIERCING_DAMAGE, and Stat.TRUE_DAMAGE
			  // else 
			  if (!shieldsApplied && isShieldGain) {
				  gainAmt = effect.getStatMods().get(Stat.SHIELD_GAIN);
				  Effect shieldEffect = new Effect(effect);
				  int shieldInt = this.randomInt() + 1;
				  shieldEffect.setInstanceId(shieldInt);
				  // TODO: this makes shields infinite, add check here later if we want them to fall off
				  shieldEffect.setDuration(-1);
				  shieldEffect.setOriginCharacter(fromCharacter.getPosition());
				  shieldEffect.setTargetCharacter(character.getPosition());
				  shieldEffect.getStatMods().remove(Stat.SHIELD_GAIN);
				  shieldEffect.getStatMods().put(Stat.SHIELDS, gainAmt);
				  shieldEffect.setDescription("This unit has " + gainAmt + " shields.");
				  current.add(shieldEffect);
			  }

		  }
		  
		  // pass by memory WAS fucking me here, huh...
		  Effect newEffect = new Effect(effect);

		  boolean isInstant = effect.getDuration() == 0;
		  
		  if (effect.getDuration() != 0) {
			  if (effect.getInstanceId() <= 0) {
				  // it's new
				  newEffect.setInstanceId(randomInt);
				  // this might actually set targets properly on AoE Effects?
				  newEffect.setOriginCharacter(fromCharacter.getPosition());
				  newEffect.setTargetCharacter(character.getPosition());
				  current.add(newEffect);
			  } else {
				  // its pre-existing

			  }
			  // it's been resolved and only lasts this turn (dmg, shield apply?)
		  }
	  }
  }
  
  public int getTotalForEnergy(Map<String, Integer> map) {
  	int counter = 0;
  	for (Map.Entry<String, Integer> e : map.entrySet()) {
  		counter = counter + e.getValue();
  	}
  	return counter;
  }
  
  public int randomInt()
  {
    return new Random().nextInt(9999999);
  }
}
