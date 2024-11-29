package br.com.saldoamigo.service;

import br.com.saldoamigo.dto.AccountDto;
import br.com.saldoamigo.exception.ResourceNotFoundException;
import br.com.saldoamigo.mapper.CustomModelMapper;
import br.com.saldoamigo.model.AccountModel;
import br.com.saldoamigo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public AccountDto create(AccountDto accountDto) {
        AccountModel accountModel = CustomModelMapper.parseObject(accountDto, AccountModel.class);
        return CustomModelMapper.parseObject(repository.save(accountModel), AccountDto.class);
    }

    public AccountDto findById(Long id) {
        AccountModel found = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Conta não encontrada!"));
        return CustomModelMapper.parseObject(found, AccountDto.class);
    }

    public AccountDto update(AccountDto accountDto) {
        AccountModel found = repository.findById(accountDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Conta não encontrada!"));

        found.setName(accountDto.getName());
        found.setPixKey(accountDto.getPixKey());
        found.setCity(accountDto.getCity());
        found.setUser(CustomModelMapper.parseObject(accountDto.getUser(), AccountModel.class).getUser());

        return CustomModelMapper.parseObject(repository.save(found), AccountDto.class);
    }

    public void delete(Long id) {
        AccountModel found = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Conta não encontrada!"));
        repository.delete(found);
    }

    public Page<AccountDto> findAll(Pageable pageable) {
        var accounts = repository.findAll(pageable);
        return accounts.map(account -> CustomModelMapper.parseObject(account, AccountDto.class));
    }

    public Page<AccountDto> findByName(String name, Pageable pageable) {
        var accounts = repository.findByNameContainingIgnoreCase(name, pageable);
        return accounts.map(account -> CustomModelMapper.parseObject(account, AccountDto.class));
    }

    public Page<AccountDto> findByCity(String city, Pageable pageable) {
        var accounts = repository.findByCityContainingIgnoreCase(city, pageable);
        return accounts.map(account -> CustomModelMapper.parseObject(account, AccountDto.class));
    }

    public Page<AccountDto> findByPixKey(String pixKey, Pageable pageable) {
        var accounts = repository.findByPixKeyContainingIgnoreCase(pixKey, pageable);
        return accounts.map(account -> CustomModelMapper.parseObject(account, AccountDto.class));
    }

    public Page<AccountDto> findByUserId(Long userId, Pageable pageable) {
        var accounts = repository.findByUserId(userId, pageable);
        return accounts.map(account -> CustomModelMapper.parseObject(account, AccountDto.class));
    }
}
