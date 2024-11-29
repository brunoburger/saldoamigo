package br.com.saldoamigo.service;

import br.com.saldoamigo.dto.AccountDto;
import br.com.saldoamigo.dto.GroupDto;
import br.com.saldoamigo.exception.ResourceNotFoundException;
import br.com.saldoamigo.mapper.CustomModelMapper;
import br.com.saldoamigo.model.GroupModel;
import br.com.saldoamigo.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    @Autowired
    private GroupRepository repository;

    public GroupDto create(GroupDto groupDto) {
        GroupModel groupModel = CustomModelMapper.parseObject(groupDto, GroupModel.class);
        return CustomModelMapper.parseObject(repository.save(groupModel), GroupDto.class);
    }

    public GroupDto findById(Long id) {
        GroupModel found = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado!"));
        return CustomModelMapper.parseObject(found, GroupDto.class);
    }

    public GroupDto update(GroupDto groupDto) {
        GroupModel found = repository.findById(groupDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado!"));
        found.setName(groupDto.getName());
        found.setDescription(groupDto.getDescription());
        return CustomModelMapper.parseObject(repository.save(found), GroupDto.class);
    }

    public void delete(Long id) {
        GroupModel found = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado!"));
        repository.delete(found);
    }

    public Page<GroupDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(group -> CustomModelMapper.parseObject(group, GroupDto.class));
    }

    public Page<GroupDto> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable).map(group -> CustomModelMapper.parseObject(group, GroupDto.class));
    }

    public Page<GroupDto> findByUserId(Long userId, Pageable pageable) {
        var groups = repository.findByUserId(userId, pageable);
        return groups.map(group -> CustomModelMapper.parseObject(group, GroupDto.class));
    }
}
