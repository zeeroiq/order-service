/*
 * Created by zeeroiq on 9/23/20, 12:42 AM
 */

package com.shri.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto extends BaseItem {

    private String customerName;

}
