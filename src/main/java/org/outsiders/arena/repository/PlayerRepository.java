package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public abstract interface PlayerRepository
  extends JpaRepository<Player, Integer>
{}
