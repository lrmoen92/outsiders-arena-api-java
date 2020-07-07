package org.outsiders.arena.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.outsiders.arena.domain.Ability;
import org.outsiders.arena.domain.Character;
import org.outsiders.arena.domain.Conditional;
import org.outsiders.arena.domain.Cost;
import org.outsiders.arena.domain.Effect;
import org.outsiders.arena.domain.Quality;
import org.outsiders.arena.domain.Stat;
import org.outsiders.arena.service.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData
  implements CommandLineRunner
{
  private static final Logger LOG = LoggerFactory.getLogger(SeedData.class);
  @Autowired
  private CharacterService characterService;
  
  public void run(String... args)
    throws Exception
  {  
    makeOutsiders();
    LOG.info("ALL DATA SEEDED SUCCESSFULLY");
  }
  
  private Map<String, Integer> buildStat(String a, Integer b) {
	  Map<String, Integer> map = new HashMap();
	  map.put(a, b);
	  return map;
  }
  
  private List<Effect> buildEffects(List<Effect> l, 
		  int duration,
		  String name,
		  String description,
		  String url,
		  boolean physical,
		  boolean magical,
		  boolean affliction,
		  boolean interruptable,
		  boolean conditional,
		  boolean visible,
		  boolean stacks,
		  Map<String, Integer> statMods,
		  String quality,
		  String condition) {
	  Effect e = new Effect(physical, magical, affliction, interruptable, conditional);
	  e.setName(name);
	  e.setDescription(description);
	  e.setAvatarUrl(url);
	  e.setDuration(duration);
	  e.setCondition(condition);
	  e.setStatMods(statMods);
	  e.setQuality(quality);
	  e.setVisible(visible);
	  e.setStacks(stacks);
	  
	  if (l == null) {
		  l = new ArrayList<>();
	  }
	  l.add(e);
	  return l;
  }
  
  private List<Ability> buildAbilities(List<Ability> l, 
		  boolean enemies, 
		  boolean allies, 
		  boolean self, 
		  boolean aoe, 
		  List<String> cost,
		  int cooldown, 
		  String name, 
		  String url, 
		  String description,
		  List<Effect> effects,
		  List<Effect> selfEffects,
		  List<Effect> allyEffects,
		  List<Effect> aoeEnemyEffects,
		  List<Effect> aoeAllyEffects){
	  Ability a = new Ability(enemies, allies, self, aoe);
	  a.setCooldown(cooldown);
	  a.setName(name);
	  a.setAbilityUrl(url);
	  a.setCost(cost);
	  a.setEnemyEffects(effects);
	  a.setSelfEffects(selfEffects);
	  a.setAllyEffects(allyEffects);
	  a.setAoeEnemyEffects(aoeEnemyEffects);
	  a.setAoeAllyEffects(aoeAllyEffects);
	  a.setDescription(description);
	  
	  if (l == null) {
		  l = new ArrayList<>();
	  }
	  l.add(a);
	  return l;
  }
  
  private void makeOutsiders() {
	 LOG.info("Saved new Character: " + makeAlex().toString());
	 LOG.info("Saved new Character: " + makeFainne().toString());
	 LOG.info("Saved new Character: " + makeShinzo().toString());
	 LOG.info("Saved new Character: " + makeHollyanna().toString());
	 LOG.info("Saved new Character: " + makeGeddy().toString());
  }
  
  
  private Character makeAlex() {
	  // EFFECTS 1
	  List<Effect> effects = buildEffects(null, 3, 
			  "Impulse", "This unit is taking 10 affliction damage", "/assets/alex1.png",
			  false, false, true, true, false, true, false,
			  buildStat(Stat.DAMAGE, 10), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.oneRan, 4,
			  "Impulse", "/assets/alex1.png",
			  "Alex uses Impulse to attack one enemy, dealing 10 points of affliction damage for 3 turns.",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 1,
			  "Flash Freeze", "This unit took damage from Flash Freeze", "/assets/alex2.png",
			  false, true, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  effects1 = buildEffects(effects1, 2,
			  "Flash Frozen", "This unit will do 10 less damage", "/assets/alex2.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE_OUT, -10), null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneDex, 2,
			  "Flash Freeze", "/assets/alex2.png",
			  "Alex freezes one enemy with Flash Freeze, dealing 20 magic damage, and reducing their damage by 10 for 2 turns",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Study Target", "This unit takes 10 more damage from all sources", "/assets/alex3.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE_IN, 10), null, null);
	  
	  effects2 = buildEffects(effects2, 1,
			  "Study Target", "This unit's traps have been revealed", "/assets/alex3.png",
			  true, false, false, false, false, true, false,
			  null, Quality.REVEALED, null);

	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoRan, 3,
			  "Study Target", "/assets/alex3.png",
			  "Alex reveals and disables any hidden skills on the target, and increases the damage target takes from all sources by 10 for 2 turns.",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Stealth", "Alex is invunerable", "/assets/alex4.png",
			  true, false, false, false, false, true, false,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Stealth", "/assets/alex4.png",
			  "Alex hides for a turn, going invulnerable",
			  null, effects3, null, null, null);	  
	  
	  Character c = new Character();
	  c.setId(1);
	  c.setName("Alex Drake");
	  c.setAvatarUrl("https://i.imgur.com/L0FbVol.png");
	  c.setSlot1(test.get(0));
	  c.setSlot2(test.get(1));
	  c.setSlot3(test.get(2));
	  c.setSlot4(test.get(3));
	  
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
  
  private Character makeFainne() {
	  // EFFECTS 1
	  List<Effect> effects = buildEffects(null, 2, 
			  "Unyielding Spirit", "This unit will gain 1 Random Energy if attacked", "/assets/fainne1.png",
			  false, true, false, true, true, false, false,
			  buildStat(Stat.ENERGY_CHANGE, 1), null, Conditional.TARGET_WAS(Conditional.ATTACKED));
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  false, true, false, false,
			  Cost.oneDiv, 3,
			  "Unyielding Spirit", "/assets/fainne1.png",
			  "Fainne's unyielding spirit supplies 1 random energy to an ally if they are attacked, for 2 turns",
			  null, null, effects, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 2,
			  "Call Lightning", "This unit is taking 20 damage", "/assets/fainne2.png",
			  true, true, false, true, false, true, false,
			  buildStat(Stat.DAMAGE, 20), Quality.AFFECTED_BY("Call Lightning"), null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoRan, 3,
			  "Call Lightning", "/assets/fainne2.png",
			  "Fainne summons bolts of lighting from above, dealing 20 damage to one enemy for two turns",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 1,
			  "Zahra", "This unit took extra damage from Zahra", "/assets/fainne3.png",
			  true, false, false, false, true, true, false, 
			  buildStat(Stat.BONUS_DAMAGE, 15), null, Conditional.TARGET_AFFECTED_BY("Call Lightning"));
	  
	  effects2 = buildEffects(effects2, 1,
			  "Zahra", "This unit took damage from Zahra", "/assets/fainne3.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneStr, 0,
			  "Zahra", "/assets/fainne3.png",
			  "Faine lets loose Zahra to maim one enemy for 20 dmg.  15 extra damage if target is under the effects of Call Lightning",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Irethis", "Fainne calls for Irethis' aid, going invulnerable", "/assets/fainne4.png",
			  true, false, false, false, false, true, false,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Irethis", "/assets/fainne4.png",
			  "Fainne calls for Irethis' aid, going invulnerable",
			  null, effects3, null, null, null);  
	  
	  Character c = new Character();
	  c.setId(2);
	  c.setName("Fainne");
	  c.setAvatarUrl("https://i.imgur.com/z02peKO.png");
	  c.setSlot1(test.get(0));
	  c.setSlot2(test.get(1));
	  c.setSlot3(test.get(2));
	  c.setSlot4(test.get(3));
	  
	  c = this.characterService.save(c);
	  return c;
  }
  
  private Character makeShinzo() {
	  // EFFECTS 1
	  List<Effect> effects = buildEffects(null, 1, 
			  "Trident", "Shinzo has dealt physical damage with his trident",  "/assets/shinzo1.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 25), null, null);

	  effects = buildEffects(effects, 1, 
			  "Trident", "Shinzo has dealt magical damage with his trident",  "/assets/shinzo1.png",
			  false, true, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.oneStrOneArc, 2,
			  "Trident", "/assets/shinzo1.png",
			  "Shinzo brings the storm with his trident, doing 25 physical damage and 20 magical damage",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 2,
			  "Entangle", "This unit is taking 15 damage", "/assets/shinzo2.png",
			  false, true, false, true, false, true, false,
			  buildStat(Stat.DAMAGE, 15), null, null);
	  
	  effects1 = buildEffects(effects1, 1,
			  "Entangle", "This unit is stunned", "/assets/shinzo2.png",
			  false, true, false, false, false, true, false,
			  null, Quality.STUNNED, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneDivOneRan, 4,
			  "Entangle", "/assets/shinzo2.png",
			  "Shinzo entangles an enemy doing 15 damage for two turns, and stunning them for one.",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Channel Energy", "This unit is healing 10 HP", "/assets/shinzo3.png",
			  false, true, false, true, false, true, false,
			  buildStat(Stat.DAMAGE, -10), null, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  false, true, true, true,
			  Cost.oneDiv, 4,
			  "Channel Energy", "/assets/shinzo3.png",
			  "Shinzo calls upon his divine power and heals his party for 10 hp for two turns",
			  null, null, null, null, effects2);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Captain's Orders", "Shinzo's the Captain.  He's invulnerable this turn.", "/assets/shinzo4.png",
			  true, false, false, false, false, true, false,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Captain's Orders", "/assets/shinzo4.png",
			  "Shinzo call's upon his status among the seas to become invulnerable this turn.",
			  null, effects3, null, null, null);  
	  
	  Character c = new Character();
	  c.setId(3);
	  c.setName("Shinzo Katetsu");
	  c.setAvatarUrl("https://i.imgur.com/onwlaKJ.png");
	  c.setSlot1(test.get(0));
	  c.setSlot2(test.get(1));
	  c.setSlot3(test.get(2));
	  c.setSlot4(test.get(3));
	  
	  c = this.characterService.save(c);
	  return c;
  }
  
  private Character makeHollyanna() {
	  // EFFECTS 1
	  List<Effect> effects = buildEffects(null, 1, 
			  "Sneak Attack", "Sneak Attack Damage",  "/assets/holly1.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 15), null, null);
	  
	  effects = buildEffects(effects, 1, 
			  "Sneak Attack", "Sneak Attack Bonus Damage",  "/assets/holly1.png",
			  true, false, false, false, true, true, false,
			  buildStat(Stat.BONUS_DAMAGE, 10), null, Conditional.TARGET_HAS_QUALITY(Quality.STUNNED));
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.oneRan, 2,
			  "Sneak Attack", "/assets/holly1.png",
			  "Hollyanna hides in the shadows, and then strikes, dealing 15 damage, and 10 bonus damage if the target is stunned.",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 1,
			  "Eliminate", "Hollyanna uses raw force to inflict a mortal wound.", "/assets/holly2.png",
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 40), null, null);
	  
	  List<Effect> selfEffects1 = buildEffects(null, 2,
			  "Eliminate - Bloodbath", "Hollyanna is being baptised in blood, Fulfill Contract is empowered until the end of next turn.", "/assets/holly2.png",
			  true, false, false, false, true, true, false,
			  null, Quality.AFFECTED_BY("Bloodbath"), Conditional.USER_DID(Conditional.KILL));
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, true, false,
			  Cost.oneStrOneDex, 3,
			  "Eliminate", "/assets/holly2.png",
			  "Hollyanna uses raw force to inflict a mortal wound, dealing 40 damage.  If this skill kills an enemy, Fulfill Contract is empowered next turn.",
			  effects1, selfEffects1, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 1,
			  "Fulfill Contract", "Take part in an evil deed, stunning all enemies for one turn.", "/assets/holly3.png",
			  false, true, false, false, false, true, false,
			  null, Quality.STUNNED, null);
	  
	  List<Effect> selfEffects2 = buildEffects(null, 1,
			  "Fulfill Contract", "Hollyanna is untargetable.", "/assets/holly3.png",
			  false, true, false, false, true, true, false,
			  buildStat(Stat.DAMAGE, -40), Quality.UNTARGETABLE, Conditional.USER_AFFECTED_BY("Bloodbath"));
	  
	  selfEffects2 = buildEffects(selfEffects2, 1,
			  "Fulfill Contract", "Hollyanna is stunned.", "/assets/holly3.png",
			  false, true, false, false, true, true, false,
			  null, Quality.STUNNED, Conditional.USER_AFFECTED_BY("Bloodbath"));
	  
	  selfEffects2 = buildEffects(selfEffects2, -1,
			  "Fulfill Contract", "If this skill is used 3 times, Hollyanna dies.", "/assets/holly3.png",
			  false, true, false, false, false, true, true,
			  null, Quality.AFFECTED_BY("Fulfill Contract"), null);
	  
	  selfEffects2 = buildEffects(selfEffects2, 0,
			  "Fulfill Contract", "If this skill is used 3 times, Hollyanna dies.", "/assets/holly3.png",
			  false, true, false, false, true, true, false,
			  buildStat(Stat.TRUE_DAMAGE, 100), null, Conditional.USER_AFFECTED_BY("Fulfill Contract", 3));
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, true, true,
			  Cost.twoDiv, 2,
			  "Fulfill Contract", "/assets/holly3.png",
			  "Take part in an evil deed, stunning all enemies for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  null, selfEffects2, null, effects2, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Shadow Illusion", "Hollyanna hides in shadow", "/assets/holly4.png",
			  true, false, false, false, false, true, false,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Shadow Illusion", "/assets/holly4.png",
			  "Hollyanna hides in shadow, going invulnerable for 1 turn",
			  null, effects3, null, null, null);  
	  
	  Character c = new Character();
	  c.setId(4);
	  c.setName("Hollyanna Knox");
	  c.setAvatarUrl("https://i.imgur.com/SeMAoFS.png");
	  c.setSlot1(test.get(0));
	  c.setSlot2(test.get(1));
	  c.setSlot3(test.get(2));
	  c.setSlot4(test.get(3));
	  
	  c = this.characterService.save(c);
	  return c;
  }
  
  private Character makeGeddy() {
	  // EFFECTS 1
	  List<Effect> effects = buildEffects(null, 1, 
			  "Darts", "This unit took damage from darts", "/assets/holly4.png", 
			  true, false, false, false, false, true, false,
			  buildStat(Stat.DAMAGE, 15), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, true,
			  Cost.oneDex, 1,
			  "Darts", "/assets/geddy1.png",
			  "Geddy tosses darts at all enemies, dealing 15 damage",
			  null, null, null, effects, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 2,
			  "Inspiration", "This unit will gain 10 shields.", "/assets/geddy2.png",
			  true, false, false, true, false, true, false,
			  buildStat(Stat.SHIELD_GAIN, 10), null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  false, true, false, false,
			  Cost.free, 4,
			  "Inspiration", "/assets/geddy2.png",
			  "Geddy inspires an ally for 2 turns, giving them 10 shields each turn.",
			  null, null, effects1, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Geas", "This unit is following their Geas, and cannot become invulnerable.", "/assets/geddy3.png",
			  false, true, false, false, false, true, false,
			  null, Quality.VULNERABLE, null);
	  
	  effects2 = buildEffects(effects2, 2,
			  "Geas", "This unit is following their Geas, and is stunned.", "/assets/geddy3.png",
			  false, true, false, false, false, true, false,
			  null, Quality.STUNNED, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoArc, 4,
			  "Geas", "/assets/geddy3.png",
			  "Geddy gives one unit a quest, causing them to be stunned and unable to become invulnerable for 2 turns.",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Distracting Performance", "Geddy is being loud.  He's invulnerable.", "/assets/geddy4.png",
			  true, false, false, false, false, true, false,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Distracting Performance", "/assets/geddy4.png",
			  "Geddy makes a scene, going invulnerable",
			  null, effects3, null, null, null);	  
	  
	  Character c = new Character();
	  c.setId(5);
	  c.setName("Geddy Splintwalker");
	  c.setAvatarUrl("https://i.imgur.com/EtNEXcG.png");
	  c.setSlot1(test.get(0));
	  c.setSlot2(test.get(1));
	  c.setSlot3(test.get(2));
	  c.setSlot4(test.get(3));
	  
	  c = this.characterService.save(c);
	  return c;
  }
}
