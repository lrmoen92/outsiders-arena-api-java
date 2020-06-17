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
		  boolean physical,
		  boolean magical,
		  boolean affliction,
		  boolean interruptable,
		  boolean conditional,
		  boolean visible,
		  Map<String, Integer> statMods,
		  String quality,
		  String condition) {
	  Effect e = new Effect(physical, magical, affliction, interruptable, conditional);
	  e.setName(name);
	  e.setDescription(description);
	  e.setDuration(duration);
	  e.setCondition(condition);
	  e.setStatMods(statMods);
	  e.setQuality(quality);
	  e.setVisible(visible);
	  
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
			  "Impulse", "This unit is taking Shock Damage from Impulse", 
			  true, true, true, true, false, true, 
			  buildStat(Stat.DAMAGE, 10), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.oneRan, 4,
			  "Impulse", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Alex uses Impulse to attack one enemy, dealing 10 points of damage for 3 turns.",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 0,
			  "Flash Freeze", "This unit took damage from Flash Freeze",
			  true, true, true, false, false, true,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  effects1 = buildEffects(effects1, 2,
			  "Flash Frozen", "This unit has been frozen by Flash Freeze and will do less damage",
			  true, false, true, false, false, true,
			  buildStat(Stat.DAMAGE_OUT, -10), null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneDex, 2,
			  "Flash Freeze", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Freeze and damage one enemy with Flash Freeze, reducing their damage by 10 for 2 turns, and dealing 20 damage",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Study Target - Traps", "This unit has been revealed by traps",
			  true, false, false, false, false, false, 
			  null, Quality.REVEALED, null);
	  
	  effects2 = buildEffects(effects2, 2,
			  "Study Target - Weakness", "This unit's weakness has been revealed, and takes 10 more damage from all sources for 2 turns",
			  true, false, false, false, false, true,
			  buildStat(Stat.DAMAGE_IN, 10), null, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoRan, 4,
			  "Study Target", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Study the target to learn its weaknesses and warn you of any traps, and increases the damage it takes from all sources by 10 for one turn",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Stealth", "Alex hides for a turn",
			  true, false, false, false, false, true,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Stealth", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
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
			  "Pure of Heart", "This unit will gain Energy if attacked", 
			  false, true, false, true, true, false, 
			  buildStat(Stat.ENERGY_CHANGE, 1), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  false, true, false, false,
			  Cost.oneDiv, 3,
			  "Pure of Heart", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Fainne's pure nature keeps her allies fighting on by supplying energy when they are attacked",
			  null, null, effects, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 2,
			  "Beast Shape", "This unit is taking damage from Beast Shape",
			  true, true, false, true, false, true,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoRan, 3,
			  "Beast Shape", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Fainne turns into a medley of deadly beasts, dealing 20 damage to one enemy for two turns",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 0,
			  "Zahra", "This unit took extra damage from Zahra",
			  true, false, false, false, true, true, 
			  buildStat(Stat.BONUS_DAMAGE, 15), null, Conditional.AFFECTED_BY("Beast Shape"));
	  effects2 = buildEffects(effects2, 0,
			  "Zahra", "This unit took damage from Zahra",
			  true, false, false, false, false, true, 
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneStr, 0,
			  "Zahra", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Faine lets loose Zahra to maim one enemy for 20 dmg.  15 extra if target is under effects of Beast Shape",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Irethis", "Fainne calls for Irethis' aid, going invulnerable",
			  true, false, false, false, false, true,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Irethis", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
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
	  List<Effect> effects = buildEffects(null, 0, 
			  "Trident", "Shinzo has dealt damage with his trident", 
			  true, true, false, false, false, true, 
			  buildStat(Stat.DAMAGE, 35), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.oneStrOneRan, 2,
			  "Trident", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Shinzo brings the storm with his trident, doing 35 damage",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 2,
			  "Call Lightning", "This unit is taking damage from Call Lightning",
			  false, true, false, true, false, true,
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  effects1 = buildEffects(effects1, 1,
			  "Call Lightning", "This unit is stunned from Call Lightning",
			  false, true, false, false, false, true,
			  null, Quality.STUNNED, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.oneDivOneRan, 4,
			  "Call Lightning", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Shinzo calls down lightning for two turns, dealing 20 damage each turn, and stunning the target for one",
			  effects1, null, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Channel Energy", "This unit is healing from Channel Energy",
			  false, true, false, true, false, true, 
			  buildStat(Stat.DAMAGE, -10), null, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  false, true, true, true,
			  Cost.oneDiv, 3,
			  "Channel Energy", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Shinzo calls upon his divine power and heals his party for 10 hp for two turns",
			  null, null, null, null, effects2);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Captain's Orders", "Shinzo's the Captain.  Can't hurt him this turn.",
			  true, false, false, false, false, true,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Captain's Orders", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
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
	  List<Effect> effects = buildEffects(null, 0, 
			  "Sneak Attack", "Sneak Attack Damage", 
			  true, false, false, false, false, true, 
			  buildStat(Stat.DAMAGE, 20), null, null);
	  
	  effects = buildEffects(effects, 0, 
			  "Sneak Attack", "Sneak Attack Damage", 
			  true, false, false, false, true, true, 
			  buildStat(Stat.BONUS_DAMAGE, 20), null, Conditional.HAS_QUALITY(Quality.STUNNED));
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, false,
			  Cost.twoRan, 4,
			  "Sneak Attack", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Hollyanna hides in the shadows, and then strikes, dealing 20 damage, and 20 bonus damage if the target is stunned.",
			  effects, null, null, null, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 0,
			  "Eliminate", "Hollyanna uses raw force to inflict a mortal wound.",
			  true, false, false, false, false, true,
			  buildStat(Stat.DAMAGE, 40), null, null);
	  
	  List<Effect> selfEffects1 = buildEffects(null, 2,
			  "Eliminate - Blood Bath", "Hollyanna is being baptised in blood, Fulfill Contract is empowered until the end of next turn.",
			  true, false, false, false, true, true,
			  null, null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  true, false, true, false,
			  Cost.oneStrOneDex, 2,
			  "Eliminate", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Hollyanna uses raw force to inflict a mortal wound, dealing 40 damage.  If this skill kills an enemy, Fulfill Contract is empowered next turn.",
			  effects1, selfEffects1, null, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 1,
			  "Fulfill Contract", "Take part in an evil deed, stunning an enemy for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  false, true, false, false, false, true, 
			  null, Quality.STUNNED, null);
	  
	  List<Effect> selfEffects2 = buildEffects(null, 1,
			  "Fulfill Contract", "Take part in an evil deed, stunning an enemy for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  false, true, false, false, true, true,
			  buildStat(Stat.DAMAGE, -40), Quality.UNTARGETABLE + "_" + Quality.STUNNED, Conditional.AFFECTED_BY("BloodBath"));
	  
	  selfEffects2 = buildEffects(selfEffects2, -1,
			  "Fulfill Contract", "Take part in an evil deed, stunning an enemy for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  false, true, false, false, false, true,
			  null, Quality.AFFECTED_BY("Fulfill Contract"), null);
	  
	  selfEffects2 = buildEffects(selfEffects2, 0,
			  "Fulfill Contract", "Take part in an evil deed, stunning an enemy for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  false, true, false, false, true, true,
			  buildStat(Stat.TRUE_DAMAGE, 100), null, Conditional.AFFECTED_BY("Fulfill Contract3"));
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, true, false,
			  Cost.oneDiv, 2,
			  "Fulfill Contract", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Take part in an evil deed, stunning an enemy for one turn.  If Bloodbath is active, heal 40 hp, and become stunned and untargetable for one turn.  If this skill is used 3 times, Hollyanna dies.",
			  effects2, selfEffects2, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Shadow Illusion", "Hollyanna hides in shadow",
			  true, false, false, false, false, true,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Stealth", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
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
	  List<Effect> effects = buildEffects(null, 0, 
			  "Darts", "This unit took damage from darts", 
			  true, false, false, false, false, true, 
			  buildStat(Stat.DAMAGE, 15), null, null);
	  
	  // ABILITY 1
	  List<Ability> test = buildAbilities(null,
			  true, false, false, true,
			  Cost.oneRan, 1,
			  "Darts", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Geddy tosses darts at all enemies, dealing 15 damage",
			  null, null, null, effects, null);
	  
	  // EFFECTS 2
	  List<Effect> effects1 = buildEffects(null, 3,
			  "Inspiration", "This unit will gain 10 shields.",
			  true, false, false, true, false, true,
			  buildStat(Stat.SHIELD_GAIN, 10), null, null);
	  
	  // ABILITY 2
	  test = buildAbilities(test,
			  false, true, false, false,
			  Cost.free, 4,
			  "Inspiration", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Geddy inspires an ally for 3 turns, giving them 10 shields each turn.",
			  null, null, effects1, null, null);
	  
	  // EFFECTS 3
	  List<Effect> effects2 = buildEffects(null, 2,
			  "Geas", "This unit is following their Geas, and cannot become invulnerable.",
			  false, true, false, false, false, true, 
			  null, Quality.VULNERABLE, null);
	  effects2 = buildEffects(effects2, 2,
			  "Geas", "This unit is following their Geas, and is stunned.",
			  false, true, false, false, false, true, 
			  null, Quality.STUNNED, null);
	  
	  // ABILITY 3
	  test = buildAbilities(test,
			  true, false, false, false,
			  Cost.twoArc, 4,
			  "Geas", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
			  "Geddy gives one unit a quest, causing them to be stunned and unable to become invulnerable for 2 turns.",
			  effects2, null, null, null, null);
	  
	  // EFFECTS 4
	  List<Effect> effects3 = buildEffects(null, 1,
			  "Distracting Performance", "Geddy is being loud.  He's invulnerable.",
			  true, false, false, false, false, true,
			  null, Quality.INVULNERABLE, null);
	  
	  // ABILITY 4
	  test = buildAbilities(test,
			  false, false, true, false,
			  Cost.oneRan, 4,
			  "Distracting Performance", "https://emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/dagger-knife_1f5e1.png",
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
