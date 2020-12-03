CREATE KEYSPACE outsiders_arena WITH REPLICATION = {
    'class':'SimpleStrategy',
	'replication_factor':1
};

CREATE TYPE outsiders_arena.battleeffect (
    duration int,
    avatarurl text,
    instanceid int,
    groupid int,
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

CREATE TYPE outsiders_arena.missionrequirement (
    amount int,
    missionid int,
    userfaction text,
    targetfaction text
);

CREATE TYPE outsiders_arena.missionprogress (
    requirements list<frozen<missionrequirement>>
);

CREATE TYPE outsiders_arena.characterinstance (
    hp int,
    position int,
    cooldowns list<int>,
    playeronecharacter boolean,
    location int,
    characterid int,
    dead boolean,
    effects list<frozen<battleeffect>>,
    flags list<text>
);

CREATE TYPE outsiders_arena.ability (
    cooldown int,
    abilityurl text,
    name text,
    description text,
    position int,
    targets text,
    types text,
    aoe boolean,
    self boolean,
    ally boolean,
    enemy boolean,
    cost list<text>,
    selfeffects list<frozen<battleeffect>>,
    enemyeffects list<frozen<battleeffect>>,
    aoeenemyeffects list<frozen<battleeffect>>,
    allyeffects list<frozen<battleeffect>>,
    aoeallyeffects list<frozen<battleeffect>>
);

CREATE TYPE outsiders_arena.playercredentials (
    email text,
    password text
);

CREATE TABLE outsiders_arena.character (
    id int PRIMARY KEY,
    avatarurl text,
    name text,
    description text,
    factions list<text>,
    abilities list<frozen<ability>>
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

CREATE TABLE outsiders_arena.mission (
    id int PRIMARY KEY,
    name text,
    description text,
    avatarurl text,
    minmumlevel int,
    prerequisitemissionid int,
    characteridunlocked int,
    requirements list<frozen<missionrequirement>>
);

CREATE TABLE outsiders_arena.player (
    id int PRIMARY KEY,
    displayname text,
    avatarurl text,
    credentials frozen<playercredentials>,
    level int,
    xp int,
    characteridsunlocked list<int>,
    missionidscompleted list<int>,
    missionprogress list<frozen<missionprogress>>
);

CREATE INDEX battle_playeridone on outsiders_arena.battle (playeridone);