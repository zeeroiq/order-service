/*
<<<<<<< HEAD:src/main/java/com/shri/model/BeerPagedList.java
 * Created by zeeroiq on 9/23/20, 12:40 AM
=======
 * Created by zeeroiq on 9/19/20, 3:15 AM
>>>>>>> 6ebe9aa1787ba621fcb6e1f71e75f0d20a751182:src/main/java/com/shri/beerservice/model/BeerPagedList.java
 */

/*
 * Created by  ZeeroIQ on 9/15/20, 1:54 AM
 */

package com.shri.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public class CustomerPagedList extends PageImpl<CustomerDto> implements Serializable {

    private static final long serialVersionUID = -429487477786964934L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomerPagedList(@JsonProperty("content") List<CustomerDto> content,
                             @JsonProperty("number") int number,
                             @JsonProperty("size") int size,
                             @JsonProperty("totalElements") long totalElements,
                             @JsonProperty("pageable") JsonNode pageable,
                             @JsonProperty("last") boolean last,
                             @JsonProperty("totalPages") int totalPages,
                             @JsonProperty("sort") JsonNode sort,
                             @JsonProperty("first") boolean first,
                             @JsonProperty("numberOfElements") int numberOfProperty) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public CustomerPagedList(List<CustomerDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomerPagedList(List<CustomerDto> content) {
        super(content);
    }
}
