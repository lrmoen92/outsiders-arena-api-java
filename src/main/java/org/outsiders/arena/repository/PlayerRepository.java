package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Player;
import org.springframework.data.cassandra.repository.CassandraRepository;

public abstract interface PlayerRepository
  extends CassandraRepository<Player, Integer>
{}
