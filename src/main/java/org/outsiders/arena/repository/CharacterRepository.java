package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Character;
import org.springframework.data.cassandra.repository.CassandraRepository;

public abstract interface CharacterRepository
  extends CassandraRepository<Character, Integer>
{}
