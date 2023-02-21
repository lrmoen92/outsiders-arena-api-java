package org.outsiders.arena.repository;

import java.util.ArrayList;
import java.util.List;

import org.outsiders.arena.domain.Combatant;
import org.springframework.stereotype.Service;

@Service
public class Characters {

	private List<Combatant> characters = new ArrayList<>();

	public Characters() {
	}

	public List<Combatant> getAll() {
		return this.characters;
	}

	public Combatant addCharacter(Combatant c) {
		this.characters.add(c);
		return c;
	}

	public Combatant getCharacter(int id) {
		return this.characters.get(id);
	}
}
