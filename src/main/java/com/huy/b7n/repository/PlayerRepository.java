package com.huy.b7n.repository;

import com.huy.b7n.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByPlayerCode(String playerCode);

    boolean existsByPlayerCode(String playerCode);

    List<PlayerEntity> findAllByPlayerCodeIn(Collection<String> playerCodes);

    List<PlayerEntity> findAllByOrderByPlayerCodeAsc();
}
