package br.com.saldoamigo.repository;

import br.com.saldoamigo.model.GroupModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupModel, Long> {
    Page<GroupModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<GroupModel> findAll(Pageable pageable);
    Page<GroupModel> findByUserId(Long userId, Pageable pageable);
}
