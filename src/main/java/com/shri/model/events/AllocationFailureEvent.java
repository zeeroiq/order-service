/*
 * Created by zeeroiq on 9/24/20, 1:43 AM
 */

package com.shri.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationFailureEvent {

    private UUID orderId;
}
