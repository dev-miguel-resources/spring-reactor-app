package mx.txalcala.spring_reactor_app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetail {

    private int quantity;
    private Dish dish;

}
