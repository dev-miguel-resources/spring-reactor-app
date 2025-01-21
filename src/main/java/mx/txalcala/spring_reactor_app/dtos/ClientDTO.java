package mx.txalcala.spring_reactor_app.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDTO {

    private String id;
    private String lastName;
    private String surname;
    private LocalDate birthDateClient;
    private String picture;

}
