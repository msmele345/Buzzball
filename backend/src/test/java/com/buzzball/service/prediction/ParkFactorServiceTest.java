package com.buzzball.service.prediction;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ParkFactorServiceTest {

    @InjectMocks
    private ParkFactorService parkFactorService;

    private static Stream<Arguments> provideParkFactorValues() {
        return Stream.of(
                Arguments.of("coors-field", 1.21),
                Arguments.of("great-american-ball-park", 1.10),
                Arguments.of("globe-life-field", 1.07),
                Arguments.of("yankee-stadium", 1.05),
                Arguments.of("fenway-park", 1.04),
                Arguments.of("wrigley-field", 1.03),
                Arguments.of("oracle-park", .93)
        );
    }

    @MethodSource("provideParkFactorValues")
    @ParameterizedTest
    void fetchesParkFactor(String parkName, double expectedFactor) {
        double actual = parkFactorService.getParkFactor(parkName);
        assertThat(actual).isEqualTo(expectedFactor);
    }
}