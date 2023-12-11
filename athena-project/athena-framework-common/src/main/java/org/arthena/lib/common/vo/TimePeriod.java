package org.arthena.lib.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 时间区间
 *
 * @author zhouzhitong
 * @since 2022/12/27
 */
@Data
public class TimePeriod {

    /**
     * 开始时间
     */
    @JsonFormat( pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

}
