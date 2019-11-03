package com.atstudio.volatileweatherbot.services.impl;

import com.atstudio.volatileweatherbot.models.CityDto;
import com.atstudio.volatileweatherbot.services.api.CityResolverService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class CityResolverServiceImpl implements CityResolverService {

    @Override
    public List<CityDto> getCities(String city) {
        int cnt = 1 + ThreadLocalRandom.current().nextInt(3);
        return IntStream.range(1, cnt).mapToObj((num) -> rndCity()).collect(toList());
    }

    private CityDto rndCity() {
        String code = RandomStringUtils.randomAlphabetic(7);
        return new CityDto(code, "Displayed " + code);
    }
}
