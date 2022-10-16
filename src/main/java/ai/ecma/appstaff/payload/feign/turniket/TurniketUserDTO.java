package ai.ecma.appstaff.payload.feign.turniket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurniketUserDTO {

    private Integer id;

    private String name;

    private String gender;//male or female

    private String photoId;//attachment id in attachment service

}
