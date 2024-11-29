package br.com.saldoamigo.dto;

import br.com.saldoamigo.model.AccountModel;
import br.com.saldoamigo.model.GroupModel;
import br.com.saldoamigo.model.TransactionModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDto extends RepresentationModel<TransactionDto> {
    private long id;
    private double value;
    private LocalDate date;
    private AccountModel account;
    private GroupModel group;
}
