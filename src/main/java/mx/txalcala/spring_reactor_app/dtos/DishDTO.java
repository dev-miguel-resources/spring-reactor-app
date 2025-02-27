package mx.txalcala.spring_reactor_app.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishDTO {

    private String id;

    @NotNull()
    @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 caracteres") // , message = ""
    private String nameDish;

    @NotNull
    @Min(value = 1)
    @Max(value = 999)
    private Double priceDish;

    @NotNull
    private Boolean statusDish;
}
