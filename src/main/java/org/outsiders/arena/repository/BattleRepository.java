package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Battle;
import org.springframework.data.cassandra.repository.CassandraRepository;

public abstract interface BattleRepository
  extends CassandraRepository<Battle, Integer>
{}
