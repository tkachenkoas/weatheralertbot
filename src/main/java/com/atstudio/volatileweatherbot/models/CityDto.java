package com.atstudio.volatileweatherbot.models;

import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {
    private String displayedName;
    private String cityCode;
    private BigDecimal lat;
    private BigDecimal lng;

    public String hashed() {
        return DigestUtils.sha1Hex(
                Joiner.on(';').join(displayedName, lat, lng)
        );
    }
}