package com.escape.Main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escape.Main.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String>{
	
	Optional<Player> findByQRId(String QRId);
}
