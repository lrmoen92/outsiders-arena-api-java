package org.outsiders.arena.repository;

import org.outsiders.arena.domain.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public abstract interface CharacterRepository
  extends JpaRepository<Character, Integer>
{}
