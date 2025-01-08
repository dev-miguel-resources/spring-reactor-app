package mx.txalcala.spring_reactor_app.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "menus")
public class Menu {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    private String icon;

    @Field
    private String name;

    @Field
    private String url;

    @Field
    private List<String> roles;

}
