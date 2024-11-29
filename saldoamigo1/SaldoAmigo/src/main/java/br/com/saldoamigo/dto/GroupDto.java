package br.com.saldoamigo.dto;

import br.com.saldoamigo.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupDto extends RepresentationModel<GroupDto> {
    private long id;
    private String name;
    private String description;
    private UserModel user;
}
