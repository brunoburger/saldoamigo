package br.com.saldoamigo.service;

import br.com.saldoamigo.dto.TransactionDto;
import br.com.saldoamigo.exception.ResourceNotFoundException;
import br.com.saldoamigo.mapper.CustomModelMapper;
import br.com.saldoamigo.model.AccountModel;
import br.com.saldoamigo.model.GroupModel;
import br.com.saldoamigo.model.TransactionModel;
import br.com.saldoamigo.model.UserModel;
import br.com.saldoamigo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public TransactionDto create(TransactionDto transactionDto) {
        TransactionModel transactionModel = CustomModelMapper.parseObject(transactionDto, TransactionModel.class);
        return CustomModelMapper.parseObject(repository.save(transactionModel), TransactionDto.class);
    }

    public TransactionDto findById(Long id) {
        TransactionModel found = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada!"));
        return CustomModelMapper.parseObject(found, TransactionDto.class);
    }

    public TransactionDto update(TransactionDto transactionDto) {
        TransactionModel found = repository.findById(transactionDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada!"));
        found.setValue(transactionDto.getValue());
        found.setDate(transactionDto.getDate());
        found.setAccount(CustomModelMapper.parseObject(transactionDto.getAccount(), AccountModel.class));
        found.setGroup(CustomModelMapper.parseObject(transactionDto.getGroup(), GroupModel.class));
        return CustomModelMapper.parseObject(repository.save(found), TransactionDto.class);
    }

    public void delete(Long id) {
        TransactionModel found = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada!"));
        repository.delete(found);
    }

    public Page<TransactionDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(transaction -> CustomModelMapper.parseObject(transaction, TransactionDto.class));
    }

    public Page<TransactionDto> findByDate(LocalDate date, Pageable pageable) {
        return repository.findByDate(date, pageable).map(transaction -> CustomModelMapper.parseObject(transaction, TransactionDto.class));
    }

    public Page<TransactionDto> findByAccountId(Long accountId, Pageable pageable) {
        return repository.findByAccountId(accountId, pageable).map(transaction -> CustomModelMapper.parseObject(transaction, TransactionDto.class));
    }

    public Page<TransactionDto> findByGroupId(Long groupId, Pageable pageable) {
        return repository.findByGroupId(groupId, pageable).map(transaction -> CustomModelMapper.parseObject(transaction, TransactionDto.class));
    }
}
