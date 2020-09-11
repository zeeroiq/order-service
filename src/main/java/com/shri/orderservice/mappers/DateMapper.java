/*
 * Created by zeeroiq on 9/12/20, 4:20 AM
 */

package com.shri.orderservice.mappers;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    public OffsetDateTime asOffsetDateTime(Timestamp timestamp) {
        return timestamp != null
                ? OffsetDateTime.of(timestamp.toLocalDateTime().getYear(),
                timestamp.toLocalDateTime().getMonthValue(),
                timestamp.toLocalDateTime().getDayOfMonth(),
                timestamp.toLocalDateTime().getHour(),
                timestamp.toLocalDateTime().getMinute(),
                timestamp.toLocalDateTime().getSecond(),
                timestamp.toLocalDateTime().getNano(), ZoneOffset.UTC)
                : null;
    }

    public Timestamp asTimeStamp(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null
                ? Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                : null;
    }

}
