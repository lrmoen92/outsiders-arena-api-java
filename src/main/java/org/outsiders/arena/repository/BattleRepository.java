package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Battle;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface BattleRepository extends JpaRepository<Battle, Integer> {

	public Battle getByArenaId(Integer id);
}
