package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomPage<T> {
    private Collection<T> content; // Elementlar
    private Integer numberOfElements; // Current page dagi elementlar soni
    private Integer number; // Current page number
    private Integer size; // Nechta so'ragani

    private Boolean hasNext;
//    private Boolean hasNext;

    public CustomPage(int numberOfElements, int number, int size) {
        this.numberOfElements = numberOfElements;
        this.number = number;
        this.size = size;
    }

    public CustomPage(Collection<T> content, Boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}