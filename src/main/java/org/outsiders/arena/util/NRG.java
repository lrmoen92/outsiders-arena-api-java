package org.outsiders.arena.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.h2.expression.ConditionNot;
import org.h2.util.StringUtils;
import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.AbilityTargetDTO;
import org.outsiders.arena.domain.Battle;
import org.outsiders.arena.domain.BattleEffect;
import org.outsiders.arena.domain.BattleTurnDTO;
import org.outsiders.arena.domain.CharacterInstance;
import org.outsiders.arena.domain.Conditional;
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
	  if (spentEnergy.get(Energy.STRENGTH) > 0 ||
		  spentEnergy.get(Energy.DEXTERITY) > 0 ||
		  spentEnergy.get(Energy.ARCANA) > 0 ||
		  spentEnergy.get(Energy.DIVINITY) > 0) {
		  
		  spentEnergy.forEach((s,i) -> {
			 int prev = energy.get(s);
			 energy.put(s, prev - i);
		  });
	  }

	  // abilities, targets, origin character
	  List<AbilityTargetDTO> moves = dto.getAbilities();
	  // 3 ability target DTOs in the proper order
	  
	  // extract all effects from abilities 
	  
	  // order to resolve effects in (action bar)
	  
	  // instanceID is negative for chosen abilities, for the cancel button
	  // these will have instance ID's if they're duration effects
	  List<BattleEffect> effects = dto.getEffects();
	  // ^ these effects having things like TargetCharacter, OriginCharacter, InstanceID and the like, are important and therefore must be assigned when we set a new Effect
	  // THIS COMMENT HERE LOGAN VV
	  // resolve all of these effects in order, if -1 pull next abilityTargetDTO
	  int counter = 0;
	  for (BattleEffect effect : effects) {
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
			  
			  this.applyEffectToCharacter(effect, targetCharacter, originCharacter, enemyEnergy, energy, effect.getInstanceId(), false, false);
			  // this.applyEffectToCharacters ?? probably
			  // this part will come second... and I imagine be just a method or copy of what I write above
		  }
	  }
	  
	  // TODO: V  we're currently doing this for ALL effects
      // check opponent's hidden effects (non counters)
	  // (from either player, based on flags map), 
	  for (CharacterInstance c : team) {
		  for (BattleEffect e : c.getEffects()) {
			  if (!e.isVisible() && !Quality.COUNTERED.equals(e.getQuality()) && 
					  (isPlayerOne && e.getOriginCharacter() > 2 || !isPlayerOne && e.getOriginCharacter() < 3)) {
				  
				  int origin = e.getOriginCharacter();
				  int target = e.getTargetCharacter();
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
				  this.applyEffectToCharacter(e, targetCharacter, originCharacter, energy, enemyEnergy, e.getInstanceId(), true, false);
			  }
		  }
	  }
	  for (CharacterInstance c : enemyTeam) {
		  for (BattleEffect e : c.getEffects()) {
			  if (!e.isVisible() && !Quality.COUNTERED.equals(e.getQuality()) && (isPlayerOne && e.getOriginCharacter() > 2 || !isPlayerOne && e.getOriginCharacter() < 3)) {
				  
				  int origin = e.getOriginCharacter();
				  int target = e.getTargetCharacter();
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
				  this.applyEffectToCharacter(e, targetCharacter, originCharacter, energy, enemyEnergy, e.getInstanceId(), true, false);
			  }
		  }
	  }

	  // TODO: then clear the flags for next turn
	  
	  

	  
	  return battle;
  }
  
  public void applyAbilityToTargets(Ability a, List<CharacterInstance> allies, List<CharacterInstance> enemies, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, List<Integer> targetPositions, CharacterInstance self) throws Exception {
	  int randomInt = this.randomInt() + 1;
	  if(a.isAoe()) {
		  if(a.isEnemy()) {
//	    		if (enemyHasCounter or iHaveCounterFromEnemy && abilityHasDamagingEffect) {
//	      			// remove and show the counter was used 
			  		// ORR CHANGE THIS IFF TO JUST PASS A BOOLEAN TO METHOD BELOW FOR COUNTERED EFFECTS ??
//	      		} else {
	              	a.getAoeEnemyEffects().forEach(e -> {
	              		this.applyEffectToCharacters(new BattleEffect(e), enemies, self, enemyEnergy, energy, targetPositions, randomInt, true);
	              	});
//	      		}

          }
		  if (a.isAlly()) {
//			  if (iHaveCounterFromEnemy) {
//				  
//			  } else {
		          	a.getAoeAllyEffects().forEach(e -> {
		          		this.applyEffectToCharacters(new BattleEffect(e), allies, self, enemyEnergy, energy, targetPositions, randomInt, true);
		          	});
//			  }
          }
          if (a.isSelf() && !a.isAlly()) {
        	a.getSelfEffects().forEach(e -> {
          		this.applyEffectToCharacter(new BattleEffect(e), self, self, enemyEnergy, energy, randomInt, false, true);
          	});
    	  }
      } else {
      	// SINGLE TARGET


          if(a.isEnemy()) {
//	    		if (enemyHasCounter && abilityHasDamagingEffect) {
//	      			// remove and show the counter was used
//	      		} else {
		          	a.getEnemyEffects().forEach(e -> {
		          		this.applyEffectToCharacters(new BattleEffect(e), enemies, self, enemyEnergy, energy, targetPositions, randomInt, true);
		          	});
//	      		}
          }
          if (a.isAlly()) {
          	a.getAllyEffects().forEach(e -> {
          		this.applyEffectToCharacters(new BattleEffect(e), allies, self, enemyEnergy, energy, targetPositions, randomInt, true);
          	});
          }
          if (a.isSelf()) {
        	a.getSelfEffects().forEach(e -> {
          		this.applyEffectToCharacter(new BattleEffect(e), self, self, enemyEnergy, energy, randomInt, false, true);
          	});
    	  }
      }
	  
  };
  
  public void applyEffectToCharacters(BattleEffect effect, List<CharacterInstance> characters, CharacterInstance fromCharacter, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, List<Integer> targetPositions, int randomInt, boolean firstCast) {

	  for(CharacterInstance c: characters) {
		  if (targetPositions.contains(c.getPosition())) {
			  this.applyEffectToCharacter(effect, c, fromCharacter, enemyEnergy, energy, randomInt, false, firstCast);
		  }
	  }
	  
  };
  
  public void applyEffectToCharacter(BattleEffect effect, CharacterInstance targetCharacter, CharacterInstance fromCharacter, 
		  Map<String, Integer> enemyEnergy, Map<String, Integer> energy, int randomInt, boolean hiddenPass, boolean firstCast) {
	  List<BattleEffect> currentTargetEffects = targetCharacter.getEffects();
	  List<BattleEffect> currentFromEffects = fromCharacter.getEffects();
	  boolean interrupted = false;
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
	  boolean isStun = false;
	  boolean isPhysStun = false;
	  boolean isMagStun = false;
	  boolean isReveal = false;
	  
	  boolean isVisible = effect.isVisible();
	  
	  boolean isStunned = false;
	  boolean isPhysStunned = false;
	  boolean isMagStunned = false;
	  
	  boolean isShieldGain = false;
	  boolean isAffliction = false;
	  boolean shieldsApplied = false;
	  boolean targetInvulnerable = false;
	  boolean targetVulnerable = false;
	  boolean isDamagable = true;
	  
	  boolean hasQuality = false;
	  boolean hasStatMods = false;
	  boolean nonDmg = true;
	  
	  boolean atkFlag = false;
	  boolean dmgFlag = false;
	  boolean bufFlag = false;
	  boolean debFlag = false;
	  boolean kilFlag = false;
	  
	  int shields = 0;
	  int gainAmt = 0;
	  int totalIn = 0;
	  int totalAr = 0;
	  
	  for (BattleEffect e : currentFromEffects) {
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
	  for (BattleEffect e : currentTargetEffects) {
		  if (Quality.INVULNERABLE.equals(e.getQuality())) {
			  targetInvulnerable = true;
		  }
		  if (Quality.VULNERABLE.equals(e.getQuality())) {
			  targetVulnerable = true;
		  }
	  }

	  isDamagable = !targetInvulnerable || (targetInvulnerable && targetVulnerable);
	  boolean isConditional = effect.isConditional();
	  boolean passesConditional = false;
	  if (isConditional) {
		  String condition = effect.getCondition();
		  String[] conditions = condition.split("_");
		  String character = conditions[0];
		  String operator = conditions[1];
		  CharacterInstance thisChar;
		  if ("TARGET".equals(character)) {
			  thisChar = targetCharacter;
		  } else {
			  thisChar = fromCharacter;
		  }
		  
		  if ("AFFECTEDBY".equals(operator)) {
			  String effectName = conditions[2];
			  if (conditions.length == 4) {
				  int stacks = Integer.parseInt(conditions[3]);
				  for (Effect e : thisChar.getEffects()) {
					  if (Quality.AFFECTED_BY(effectName, stacks).equals(e.getQuality())) {
						  passesConditional = true;
						  break;
					  }
				  }
			  } else {
				  for (Effect e : thisChar.getEffects()) {
					  if (Quality.AFFECTED_BY(effectName).equals(e.getQuality())) {
						  passesConditional = true;
						  break;
					  }
				  }
			  }
			  
		  } else if ("IS".equals(operator)) {
			  String qualityName = conditions[2];
			  for (Effect e : thisChar.getEffects()) {
				  if (qualityName.equals(e.getQuality())) {
					  passesConditional = true;
					  break;
				  }
			  }
		  } else if ("WAS".equals(operator) || "DID".equals(operator)) {
				  String flagName = conditions[2];
				  for (String flag : thisChar.getFlags()) {
					  if (flagName.equals(flag)) {
						  passesConditional = true;
						  break;
					  }
				  }
		  }
		  // TODO: basically just this for conditionals?? and maybe even fainne's hidden one
		  // check character instance flags, which will be set below, when they occur.
		  // dont overthink this.  (just watch out for ACTUAL counters later, fainne's ability wont counter so it wont be a problem)
		  // can be a "WAS, DID, 
		  
		  // THESE WILL BE EASY
		  // AFFECTED_BY, AFFECTED_BY_stacks, and IS_QUALITY
		  // if it doesn't pass set to false and will be ignored below
	  }
	  boolean isInterruptable = effect.isInterruptable();
	  if (isInterruptable) {
		  if (isStunned) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
			  interrupted = true;
		  }
		  if(effect.isPhysical() && isPhysStunned) {
			  interrupted = true;
		  }
		  if (effect.isMagical() && isMagStunned) {
			  interrupted = true;
		  }
	  }
	  
	  // TODO: here for conditionals
	  // effect.getCondition();
	  // checking of conditionals
		  
	  
	  if (!hiddenPass && !effect.isVisible()) {
		  // dont resolve hidden effects the turn they're used.  check for them later
	  } else if (((isConditional && passesConditional) || !isConditional) && !interrupted) {
		  
		  // analyze and set character flags based on the below execution.
		  
		  if (!StringUtils.isNullOrEmpty(effect.getQuality())) {
			  hasQuality = true;
			  isPhysStun = Quality.PHYSICAL_STUNNED.equals(effect.getQuality());
			  isMagStun = Quality.MAGICAL_STUNNED.equals(effect.getQuality());
			  isStun = Quality.STUNNED.equals(effect.getQuality()) || isPhysStun || isMagStun;
			  isReveal = Quality.REVEALED.equals(effect.getQuality());
			  if (isReveal) {
				  for (BattleEffect effectEnded : currentTargetEffects) {
					  if (!effectEnded.isVisible()) {
						  // their hidden effect is ending, remove it as normal but replace it with a 1 turn blank effect that's not hidden
						  effectEnded.setDescription(effectEnded.getName() + " has ended.");
						  effectEnded.setQuality(null);
						  effectEnded.setStatMods(Collections.emptyMap());
						  effectEnded.setCondition(null);
						  effectEnded.setConditional(false);
						  // 995 code for hidden skill ending, revealed
						  effectEnded.setDuration(995);
						  effectEnded.setVisible(true);
					  }
				  }
			  }
		  }
		  if (!CollectionUtils.isEmpty(effect.getStatMods())) {
			  hasStatMods = true;
			  boolean isDamage = effect.getStatMods().get(Stat.DAMAGE) != null;
			  isDmg = isDamage ||
					  effect.getStatMods().get(Stat.PIERCING_DAMAGE) != null ||
					  effect.getStatMods().get(Stat.BONUS_DAMAGE) != null ||
					  effect.getStatMods().get(Stat.TRUE_DAMAGE) != null;
			  atkFlag = isDmg;
			  nonDmg = !isDmg;
			  if (targetCharacter.getPosition() - fromCharacter.getPosition() > 2) {
				  debFlag = nonDmg;
			  } else {
				  bufFlag = nonDmg;
			  }
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
			  for (BattleEffect e : currentFromEffects) {	
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
			  for (BattleEffect e : currentTargetEffects) {
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
					  if (e.getStatMods() != null) {
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
				  if (!isHeal && isDamagable && !isAffliction) {
					  for (BattleEffect ef : currentTargetEffects) {
						  if (ef.getStatMods() != null) {
							  if (ef.getStatMods().get(Stat.SHIELDS) != null) {
								  shields = ef.getStatMods().get(Stat.SHIELDS);
								  if (finalDamage > 0) {
									  finalDamage = finalDamage - shields;
									  if (finalDamage < 0) {
										  ef.getStatMods().put(Stat.SHIELDS, -finalDamage);
									      ef.setDescription("This unit has " + -finalDamage + " shields.");
									      finalDamage = 0;
									  } else {
										  ef.setDuration(997);
									  }
								  }
							  }
						  }
					  } 
					  if (shields > 0 && finalDamage >= 0) {
						  currentTargetEffects.removeIf(ex -> {
							  return ex.getDuration() == 997;
						  });
					  }
				  }
				  int trueDmg = 0;
				  // by putting this here, true damage trounces ALL
				  if (effect.getStatMods().get(Stat.TRUE_DAMAGE) != null) {
					  trueDmg = effect.getStatMods().get(Stat.TRUE_DAMAGE);
				  }

				  // actually set the final damage
				  int oldHP = targetCharacter.getHp();
				  // if invuln dont do any of the damage from above
				  if (!isDamagable) {
					  targetCharacter.setHp(oldHP - trueDmg);
				  } else {
					  targetCharacter.setHp(oldHP - (finalDamage + trueDmg));
				  }
				  if (oldHP > targetCharacter.getHp()) {
					  dmgFlag = true;
					  if (targetCharacter.getHp() == 0) {
						  kilFlag = true;
					  }
				  }
			  }
			  
			  // SHIELD AND ENERGY GAIN VV
			  if (isEnergyChange) {
				  Map<String, Integer> targeted;
				  if (Math.abs(targetCharacter.getPosition() - fromCharacter.getPosition()) > 2) {
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
				  BattleEffect shieldEffect = new BattleEffect(effect);
				  int shieldInt = this.randomInt() + 1;
				  shieldEffect.setInstanceId(shieldInt);
				  shieldEffect.setGroupId(randomInt);
				  // TODO: this makes shields infinite, add check here later if we want them to fall off
				  shieldEffect.setDuration(-1);
				  shieldEffect.setOriginCharacter(fromCharacter.getPosition());
				  shieldEffect.setTargetCharacter(targetCharacter.getPosition());
				  shieldEffect.getStatMods().remove(Stat.SHIELD_GAIN);
				  shieldEffect.getStatMods().put(Stat.SHIELDS, gainAmt);
				  shieldEffect.setInterruptable(false);
				  shieldEffect.setDescription("This unit has " + gainAmt + " shields.");
				  currentTargetEffects.add(shieldEffect);
			  }

		  }
		  
	  }
	  
	  if (firstCast) {
		  if (atkFlag) {
			  fromCharacter.getFlags().add(Conditional.ATTACK);
			  targetCharacter.getFlags().add(Conditional.ATTACKED);
		  }
		  if (dmgFlag) {
			  fromCharacter.getFlags().add(Conditional.DAMAGE);
			  targetCharacter.getFlags().add(Conditional.DAMAGED);
		  }
		  if (bufFlag) {
			  fromCharacter.getFlags().add(Conditional.BUFF);
			  targetCharacter.getFlags().add(Conditional.BUFFED);
		  }
		  if (debFlag) {
			  fromCharacter.getFlags().add(Conditional.DEBUFF);
			  targetCharacter.getFlags().add(Conditional.DEBUFFED);
		  }
		  if (kilFlag) {
			  fromCharacter.getFlags().add(Conditional.KILL);
			  targetCharacter.getFlags().add(Conditional.KILLED);
		  }
	  }

	  if (effect.getInstanceId() <= 0 && ((isConditional && passesConditional) || !isConditional)) {
		  // pass by memory WAS fucking me here, huh...
		  BattleEffect newEffect = new BattleEffect(effect);
		  // it's new
		  newEffect.setInstanceId(this.randomInt());
		  newEffect.setGroupId(randomInt);
		  // this might actually set targets properly on AoE Effects?
		  newEffect.setOriginCharacter(fromCharacter.getPosition());
		  newEffect.setTargetCharacter(targetCharacter.getPosition());

		  int dur = newEffect.getDuration() - 1;
		  if (dur > 0) {
//			  if (isDmg || isShield) {
			  if (!effect.isVisible()) {
				  
			  } else {
				  newEffect.setDuration(dur);  
			  }
			  
//			  }
			  currentTargetEffects.add(newEffect);
		  }
		  if (dur == 0) {
			  if (hasQuality || (hasStatMods && nonDmg && !isShieldGain)) {
				  // 999 is gonna be code for (technically 0, but ends this turn) ?? we'll try it.
				  newEffect.setDuration(999);
				  currentTargetEffects.add(newEffect);
			  }
		  }
		  // infinite effects (shield gains could move down here?  idk.  I dont love it.
		  if (dur == -2) {
			  newEffect.setDuration(-1);
			  // look for existing stackables and stack them??
			  // TODO:
			  String newQ = newEffect.getQuality();
			  if (!StringUtils.isNullOrEmpty(newQ)) {
				  if (newEffect.isStacks()) {
					  boolean set = false;
					  for (Effect c: currentTargetEffects) {
						  String curQ = c.getQuality();
						  if (!StringUtils.isNullOrEmpty(curQ) && !StringUtils.isNullOrEmpty(newQ)) {
							  if (newQ.length() < curQ.length()) {
								  String cutCurQ = curQ.substring(0, newQ.length());
								  // TODO: this is limiting this to 9 stacks
								  String curStacks = curQ.substring(curQ.length() - 1);
								  if (cutCurQ.equals(newQ) && newEffect.isStacks() ) {
									  c.setQuality(newQ + "_" + (Integer.parseInt(curStacks) + 1));
									  set = true;
								  }
							  }
						  }
					  }
					  if (!set) {
						  newEffect.setQuality(newQ + "_" + 1);
						  currentTargetEffects.add(newEffect);
					  }
				  } else {
					  currentTargetEffects.add(newEffect);
				  }
			  }
		  }
	   } else {
			  
			  int index = -1;
			  int counter = 0;
			  for (BattleEffect oldEffect : currentTargetEffects) {
				  if (effect.getInstanceId() == oldEffect.getInstanceId()) {
					  index = counter;
					  break;
				  }
				  counter++;
			  }
			  if (index == -1) {
				  // fucking i dont know... uh oh.
			  } else {
				  // its pre-existing
	
				  int dur = effect.getDuration() - 1;
				  
				  if (hiddenPass && !effect.isVisible()) {
					  if (dur > 0 && dur < 900) {
						  effect.setDuration(dur);  
						  currentTargetEffects.set(index, effect);
					  }
					  if (dur == 0) {
						  // their hidden effect is ending, remove it as normal but replace it with a 1 turn blank effect that's not hidden
						  BattleEffect effectEnded = currentTargetEffects.get(index);
						  effectEnded.setQuality(null);
						  effectEnded.setStatMods(Collections.emptyMap());
						  effectEnded.setCondition(null);
						  effectEnded.setConditional(false);
						  effectEnded.setDescription(effectEnded.getName() + " has ended.");
						  // 995 code for hidden skill ending, revealed
						  effectEnded.setDuration(995);
						  effectEnded.setVisible(true);
						  currentTargetEffects.set(index, effectEnded);
					  }
				  } else if (!hiddenPass && effect.isVisible()) {
					  if (dur > 0 && dur < 900) {
						  effect.setDuration(dur);  
						  currentTargetEffects.set(index, effect);
					  }
					  if (dur == 994) {
						  currentTargetEffects.remove(index);
					  }
					  if (dur == 0 || dur == 998) {
						  if ((hasQuality || (hasStatMods && nonDmg && !isShieldGain)) && dur == 0) {
							  // 999 is gonna be code for (technically 0, but ends this turn) ?? we'll try it.
							  effect.setDuration(999);
							  currentTargetEffects.set(index, effect);
						  } else {

							  currentTargetEffects.remove(index);
						  }
					  } else {
						  // infinite effects, dur = -1 right lol.
					  }
				  }
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
    return randomInt(99999);
  }
  
  public int randomInt(int in) {
	  return new Random().nextInt(in);
  }
}
