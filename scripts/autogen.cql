CREATE KEYSPACE outsiders_arena WITH REPLICATION = {
    'class':'SimpleStrategy',
	'replication_factor':1
};

CREATE TYPE outsiders_arena.effect (
    duration int,
    avatarurl text,
    instanceid int,
    description text,
    origincharacter int,
    targetcharacter int,
    name text,
    interruptable boolean,
    conditional boolean,
    physical boolean,
    magical boolean,
    affliction boolean,
    visible boolean,
    stacks boolean,
    condition text,
    statmods map<text, int>,
    quality text
);

CREATE TYPE outsiders_arena.characterinstance (
    hp int,
    position int,
    cooldownOne int,
    cooldownTwo int,
    cooldownThree int,
    cooldownFour int,
    playeronecharacter boolean,
    location int,
    characterid int,
    dead boolean,
    effects list<frozen<effect>>
);

CREATE TYPE outsiders_arena.ability (
    cooldown int,
    abilityurl text,
    name text,
    description text,
    aoe boolean,
    self boolean,
    ally boolean,
    enemy boolean,
    cost list<text>,
    selfeffects list<frozen<effect>>,
    enemyeffects list<frozen<effect>>,
    aoeenemyeffects list<frozen<effect>>,
    allyeffects list<frozen<effect>>,
    aoeallyeffects list<frozen<effect>>
);

CREATE TABLE outsiders_arena.character (
    id int PRIMARY KEY,
    avatarurl text,
    name text,
    slot1 frozen<ability>,
    slot2 frozen<ability>,
    slot3 frozen<ability>,
    slot4 frozen<ability>,
    slota frozen<ability>,
    slotb frozen<ability>,
    slotc frozen<ability>,
    slotd frozen<ability>
);

CREATE TABLE outsiders_arena.battle (
    id int PRIMARY KEY,
    playeronestart boolean,
    status text,
    turn int,
    arenaid int,
    playeridone int,
    playeridtwo int,
    playeroneteam list<frozen<characterinstance>>,
    playertwoteam list<frozen<characterinstance>>,
    playeroneenergy map<text, int>,
    playertwoenergy map<text, int>
);

CREATE TABLE outsiders_arena.player (
    id int PRIMARY KEY,
    displayname text,
    avatarurl text,
    level int
);

CREATE INDEX battle_playeridone on outsiders_arena.battle (playeridone);