package br.com.saldoamigo.repository;

import br.com.saldoamigo.model.TransactionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    Page<TransactionModel> findByDate(LocalDate date, Pageable pageable);

    Page<TransactionModel> findByAccountId(Long accountId, Pageable pageable);

    Page<TransactionModel> findByGroupId(Long groupId, Pageable pageable);
}
