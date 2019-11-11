package com.atstudio.volatileweatherbot.services.external


import com.atstudio.volatileweatherbot.models.dto.CityDto
import com.atstudio.volatileweatherbot.services.external.geo.GoogleBasedCityResolverService
import com.atstudio.volatileweatherbot.services.external.googlemaps.GoogleApiAccessor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.geocodingsFromFile
import static org.mockito.ArgumentMatchers.eq

class GoogleBasedCityResolverServiceTest {

    @Mock GoogleApiAccessor mockGoogleApi;

    GoogleBasedCityResolverService underTest;

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new GoogleBasedCityResolverService(mockGoogleApi)
    }

    @Test
    void singleResultTest() {
        Mockito.when(mockGoogleApi.getGeocodings(eq('single')))
                .thenReturn(geocodingsFromFile('single-result.json'))

        assert underTest.getCities('single') as Set == expectedSingle as Set
    }

    @Test
    void multipleResultTest() {
        Mockito.when(mockGoogleApi.getGeocodings(eq('multiple')))
                .thenReturn(geocodingsFromFile('multiple-result.json'))

        assert underTest.getCities('multiple') as Set == expectedMultiple as Set
    }

    List<CityDto> expectedSingle = [
            [
                    code        : 'ChIJH3w7GaZMHRURkD-WwKJy-8E',
                    shortName   : 'Tel Aviv-Yafo',
                    fullName    : 'Tel Aviv-Yafo, Israel',
                    lat         : 32.085299,
                    lng         : 34.781767
            ] as CityDto
    ]

    List<CityDto> expectedMultiple = [
            [
                    code        : 'ChIJ8aukkz5NtokRLAHB24Ym9dc',
                    shortName   : 'Alexandria',
                    fullName    : 'Alexandria, VA, USA',
                    lat         : 38.804835,
                    lng         : -77.046921
            ] as CityDto,
            [
                    code        : 'ChIJ0w9xJpHE9RQRuWvuKabN4LQ',
                    shortName   : 'Alexandria',
                    fullName    : 'Alexandria, Alexandria Governorate, Egypt',
                    lat         : 31.200092,
                    lng         : 29.918738
            ] as CityDto
    ]

}
