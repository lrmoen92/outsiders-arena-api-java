package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public abstract interface BattleRepository
  extends JpaRepository<Battle, Integer>
{}
