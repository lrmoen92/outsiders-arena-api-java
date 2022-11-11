package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public abstract interface MissionRepository
  extends JpaRepository<Mission, Integer>
{}
