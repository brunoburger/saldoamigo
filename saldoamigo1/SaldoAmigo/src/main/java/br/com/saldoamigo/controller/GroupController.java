package br.com.saldoamigo.controller;

import br.com.saldoamigo.dto.GroupDto;
import br.com.saldoamigo.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Groups", description = "Endpoint usado para operações que envolvem grupos")
@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService service;

    @PostMapping
    @Operation(summary = "Cria um novo grupo", description = "Cria um novo grupo na base de dados com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Grupo criado com sucesso", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<GroupDto> create(@RequestBody @Parameter(description = "Objeto contendo as informações do grupo a ser criado") GroupDto groupDto) {
        GroupDto group = service.create(groupDto);
        buildSelfLink(group);  // Adiciona o link do grupo
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um grupo pelo ID", description = "Retorna as informações do grupo com o ID especificado.")
    @ApiResponse(responseCode = "200", description = "Grupo encontrado", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    public ResponseEntity<GroupDto> findById(@PathVariable(name = "id") @Parameter(description = "ID do grupo a ser buscado") Long id) {
        GroupDto group = service.findById(id);
        buildSelfLink(group);  // Adiciona o link do grupo
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Atualiza as informações de um grupo", description = "Atualiza um grupo existente com as novas informações fornecidas.")
    @ApiResponse(responseCode = "200", description = "Grupo atualizado com sucesso", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<GroupDto> update(@RequestBody @Parameter(description = "Objeto contendo as informações do grupo a ser atualizado") GroupDto groupDto) {
        GroupDto group = service.update(groupDto);
        buildSelfLink(group);  // Adiciona o link do grupo
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um grupo", description = "Deleta o grupo com o ID especificado.")
    @ApiResponse(responseCode = "204", description = "Grupo deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") @Parameter(description = "ID do grupo a ser deletado") Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Busca todos os grupos com paginação", description = "Retorna uma lista de grupos com suporte a paginação e ordenação.")
    @ApiResponse(responseCode = "200", description = "Lista de grupos", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    public ResponseEntity<Page<GroupDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<GroupDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        Page<GroupDto> groups = service.findAll(pageable);
        groups.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(groups), HttpStatus.OK);
    }

    @GetMapping("/find/name/{name}")
    @Operation(summary = "Busca grupos pelo nome", description = "Retorna uma lista de grupos que correspondem ao nome fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de grupos encontrados", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    public ResponseEntity<Page<GroupDto>> findByName(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            @PathVariable String name,
            PagedResourcesAssembler<GroupDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        Page<GroupDto> groups = service.findByName(name, pageable);
        groups.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(groups), HttpStatus.OK);
    }

    @GetMapping("/find/user/{userId}")
    @Operation(summary = "Busca grupos pelo ID do usuário", description = "Retorna uma lista de grupos associados ao ID de usuário fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de grupos encontrados", content = @Content(schema = @Schema(implementation = GroupDto.class)))
    public ResponseEntity<Page<GroupDto>> findByUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<GroupDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "user"));
        Page<GroupDto> groups = service.findByUserId(userId, pageable); // Alterando para GroupDto
        groups.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(groups), HttpStatus.OK);
    }


    private void buildSelfLink(GroupDto groupDto) {
        groupDto.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).findById(groupDto.getId())
                ).withSelfRel()
        );
    }

    private void buildCollectionLink(CollectionModel<GroupDto> groupCollection) {
        groupCollection.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).findAll(0, 10, "asc", null)
                ).withRel(LinkRelation.of("COLLECTION"))
        );
    }
}
