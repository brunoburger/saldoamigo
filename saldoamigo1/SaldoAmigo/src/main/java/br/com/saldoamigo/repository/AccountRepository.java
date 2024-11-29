package br.com.saldoamigo.repository;

import br.com.saldoamigo.model.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    Page<AccountModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<AccountModel> findByCityContainingIgnoreCase(String city, Pageable pageable);

    Page<AccountModel> findAll(Pageable pageable);

    Page<AccountModel> findByPixKeyContainingIgnoreCase(String pixKey, Pageable pageable);

    Page<AccountModel> findByUserId(Long userId, Pageable pageable);
}
