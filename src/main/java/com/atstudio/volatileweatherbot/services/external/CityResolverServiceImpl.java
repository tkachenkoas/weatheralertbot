package com.atstudio.volatileweatherbot.services.external;

import com.atstudio.volatileweatherbot.models.CityDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Service
public class CityResolverServiceImpl implements CityResolverService {

    @Override
    public List<CityDto> getCities(String city) {
        return IntStream.range(1, 1 + current().nextInt(3))
                .mapToObj((num) -> rndCity(randomAlphabetic(7)))
                .collect(toList());
    }

    public static CityDto rndCity(String code) {
        Supplier<BigDecimal> rndBigDecimal = () -> {
            BigDecimal result = new BigDecimal(current().nextDouble(90));
            result.setScale(4, BigDecimal.ROUND_HALF_DOWN);
            return result;
        };
        return new CityDto("Displayed " + code, code, rndBigDecimal.get(), rndBigDecimal.get());
    }
}
