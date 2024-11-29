package br.com.saldoamigo.controller;

import br.com.saldoamigo.dto.AccountDto;
import br.com.saldoamigo.dto.GroupDto;
import br.com.saldoamigo.service.AccountService;
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

@Tag(name = "Accounts", description = "Endpoint usado para operações relacionadas a contas")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @PostMapping
    @Operation(summary = "Cria uma nova conta", description = "Cria uma nova conta na base de dados com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<AccountDto> create(@RequestBody @Parameter(description = "Objeto contendo as informações da conta a ser criada") AccountDto accountDto) {
        AccountDto account = service.create(accountDto);
        buildSelfLink(account);  // Adiciona o link da conta
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma conta pelo ID", description = "Retorna as informações da conta com o ID especificado.")
    @ApiResponse(responseCode = "200", description = "Conta encontrada", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    public ResponseEntity<AccountDto> findById(@PathVariable(name = "id") @Parameter(description = "ID da conta a ser buscada") Long id) {
        AccountDto account = service.findById(id);
        buildSelfLink(account);  // Adiciona o link da conta
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Atualiza as informações de uma conta", description = "Atualiza uma conta existente com as novas informações fornecidas.")
    @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<AccountDto> update(@RequestBody @Parameter(description = "Objeto contendo as informações da conta a ser atualizada") AccountDto accountDto) {
        AccountDto account = service.update(accountDto);
        buildSelfLink(account);  // Adiciona o link da conta
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma conta", description = "Deleta a conta com o ID especificado.")
    @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") @Parameter(description = "ID da conta a ser deletada") Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Busca todas as contas com paginação", description = "Retorna uma lista de contas com suporte a paginação e ordenação.")
    @ApiResponse(responseCode = "200", description = "Lista de contas", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    public ResponseEntity<Page<AccountDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<AccountDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        Page<AccountDto> accounts = service.findAll(pageable);
        accounts.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(accounts), HttpStatus.OK);
    }

    @GetMapping("/find/name/{name}")
    @Operation(summary = "Busca contas pelo nome", description = "Retorna uma lista de contas que correspondem ao nome fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de contas encontradas", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    public ResponseEntity<CollectionModel<AccountDto>> findByName(
            @PathVariable String name,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<AccountDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        Page<AccountDto> accounts = service.findByName(name, pageable);
        accounts.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(accounts), HttpStatus.OK);
    }

    @GetMapping("/find/city/{city}")
    @Operation(summary = "Busca contas pela cidade", description = "Retorna uma lista de contas que correspondem à cidade fornecida.")
    @ApiResponse(responseCode = "200", description = "Lista de contas encontradas", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    public ResponseEntity<Page<AccountDto>> findByCity(
            @PathVariable String city,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<AccountDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "city"));
        Page<AccountDto> accounts = service.findByCity(city, pageable);
        accounts.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(accounts), HttpStatus.OK);
    }

    @GetMapping("/find/pixKey/{pixKey}")
    @Operation(summary = "Busca contas pela chave Pix", description = "Retorna uma lista de contas que correspondem à chave Pix fornecida.")
    @ApiResponse(responseCode = "200", description = "Lista de contas encontradas", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    public ResponseEntity<Page<AccountDto>> findByPixKey(
            @PathVariable String pixKey,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<AccountDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "pixKey"));
        Page<AccountDto> accounts = service.findByPixKey(pixKey, pageable);
        accounts.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(accounts), HttpStatus.OK);
    }

    @GetMapping("/find/user/{userId}")
    @Operation(summary = "Busca contas pelo ID do usuário", description = "Retorna uma lista de contas associadas ao ID de usuário fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de contas encontradas", content = @Content(schema = @Schema(implementation = AccountDto.class)))
    public ResponseEntity<Page<AccountDto>> findByUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<AccountDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "user"));
        Page<AccountDto> accounts = service.findByUserId(userId, pageable);
        accounts.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(accounts), HttpStatus.OK);
    }

    private void buildSelfLink(AccountDto account) {
        account.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).findById(account.getId()))
                .withSelfRel()
                .withType("GET"));
    }
}
