package br.com.saldoamigo.repository;

import br.com.saldoamigo.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    public Page<UserModel> findByUsernameStartingWithIgnoreCaseOrderByUsername(String username, Pageable pageable);

    public Page<UserModel> findAll(Pageable pageable);

    UserDetails findByEmail(String email);
}
