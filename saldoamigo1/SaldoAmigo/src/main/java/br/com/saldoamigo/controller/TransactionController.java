package br.com.saldoamigo.controller;

import br.com.saldoamigo.dto.GroupDto;
import br.com.saldoamigo.dto.TransactionDto;
import br.com.saldoamigo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Transactions", description = "Endpoint usado para operações relacionadas a transações")
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping
    @Operation(summary = "Cria uma nova transação", description = "Cria uma nova transação na base de dados com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Transação criada com sucesso", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<TransactionDto> create(@RequestBody @Parameter(description = "Objeto contendo as informações da transação a ser criada") TransactionDto transactionDto) {
        TransactionDto transaction = service.create(transactionDto);
        buildSelfLink(transaction);  // Adiciona o link da transação
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma transação pelo ID", description = "Retorna as informações da transação com o ID especificado.")
    @ApiResponse(responseCode = "200", description = "Transação encontrada", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    public ResponseEntity<TransactionDto> findById(@PathVariable(name = "id") @Parameter(description = "ID da transação a ser buscada") Long id) {
        TransactionDto transaction = service.findById(id);
        buildSelfLink(transaction);  // Adiciona o link da transação
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Atualiza as informações de uma transação", description = "Atualiza uma transação existente com as novas informações fornecidas.")
    @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    public ResponseEntity<TransactionDto> update(@RequestBody @Parameter(description = "Objeto contendo as informações da transação a ser atualizada") TransactionDto transactionDto) {
        TransactionDto transaction = service.update(transactionDto);
        buildSelfLink(transaction);  // Adiciona o link da transação
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma transação", description = "Deleta a transação com o ID especificado.")
    @ApiResponse(responseCode = "204", description = "Transação deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") @Parameter(description = "ID da transação a ser deletada") Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Busca todas as transações com paginação", description = "Retorna uma lista de transações com suporte a paginação e ordenação.")
    @ApiResponse(responseCode = "200", description = "Lista de transações", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    public ResponseEntity<Page<TransactionDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<TransactionDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortDirection, "date"));
        Page<TransactionDto> transactions = service.findAll(pageable);
        transactions.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(transactions), HttpStatus.OK);
    }

    @GetMapping("/find/account/{accountId}")
    @Operation(summary = "Busca transações pela conta", description = "Retorna uma lista de transações associadas à conta fornecida.")
    @ApiResponse(responseCode = "200", description = "Lista de transações encontradas", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    public ResponseEntity<Page<TransactionDto>> findByAccountId(
            @PathVariable Long accountId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<TransactionDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortDirection, "date"));
        Page<TransactionDto> transactions = service.findByAccountId(accountId, pageable);
        transactions.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(transactions), HttpStatus.OK);
    }

    @GetMapping("/find/date/{date}")
    @Operation(summary = "Busca transações por data", description = "Retorna uma lista de transações para a data fornecida.")
    @ApiResponse(responseCode = "200", description = "Lista de transações encontradas", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    public ResponseEntity<Page<TransactionDto>> findByDate(
            @PathVariable LocalDate date,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<TransactionDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortDirection, "date"));
        Page<TransactionDto> transactions = service.findByDate(date, pageable);
        transactions.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(transactions), HttpStatus.OK);
    }

    @GetMapping("/find/group/{groupId}")
    @Operation(summary = "Busca transações pelo grupo", description = "Retorna uma lista de transações associadas ao grupo fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de transações encontradas", content = @Content(schema = @Schema(implementation = TransactionDto.class)))
    public ResponseEntity<Page<TransactionDto>> findByGroupId(
            @PathVariable Long groupId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página para paginação") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Número de itens por página") int size,
            @RequestParam(value = "direction", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)") String direction,
            PagedResourcesAssembler<TransactionDto> assembler
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortDirection, "date"));
        Page<TransactionDto> transactions = service.findByGroupId(groupId, pageable);
        transactions.forEach(this::buildSelfLink);
        return new ResponseEntity(assembler.toModel(transactions), HttpStatus.OK);
    }

    private void buildSelfLink(TransactionDto transactionDto) {
        transactionDto.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).findById(transactionDto.getId())
                ).withSelfRel()
        );
    }
}
