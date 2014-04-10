/* ========================================================================== *
 * Copyright 2014 USRZ.com and Pier Paolo Fumagalli                           *
 * -------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *  http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * ========================================================================== */
package org.usrz.libs.configurations;

import java.time.Duration;

import org.testng.annotations.Test;
import org.usrz.libs.testing.AbstractTest;

public class DurationTest extends AbstractTest {

    @Test
    public void testDuration() {
        final Configurations configurations = new ConfigurationsBuilder()
                .put("test-11", "2 days 3 hours 3 minutes 5.22 seconds")
                .put("test-12", "2 day  3 hour  3 minute  5.22 second ")
                .put("test-13", "2 d    3 hrs   3 mins    5.22 secs   ")
                .put("test-14", "2 d    3 hr    3 min     5.22 sec    ")
                .put("test-15", "2 d    3 h     3 m       5.22 s      ")
                .put("test-16", "2d3h3m5.22s")
                .put("test-17", "P2DT3H3M5.22S")
                .put("test-18", "PT51H3M5.22S")

                .put("test-21", "1 day 1 hour 1 minute 1 second")
                .put("test-22", "1 d   1 hr   1 min    1 sec   ")
                .put("test-23", "1 d   1 h    1 m      1 s     ")
                .put("test-24", "1d1h1m1s")
                .put("test-25", "P1DT1H1M1S")
                .put("test-26", "PT25H1M1S")

                .put("test-31", "1 hour 5 minutes 3.1 seconds")
                .put("test-32", "2 hrs 35 secs")
                .put("test-33", "7 min 12s")
                .put("test-34", "3 hours")
                .put("test-35", "4m")
                .put("test-36", "59s")
                .build();

        assertEquals(configurations.getDuration("test-11"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-12"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-13"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-14"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-15"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-16"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-17"), Duration.parse("PT51H3M5.22S"));
        assertEquals(configurations.getDuration("test-18"), Duration.parse("PT51H3M5.22S"));

        assertEquals(configurations.getDuration("test-21"), Duration.parse("PT25H1M1S"));
        assertEquals(configurations.getDuration("test-22"), Duration.parse("PT25H1M1S"));
        assertEquals(configurations.getDuration("test-23"), Duration.parse("PT25H1M1S"));
        assertEquals(configurations.getDuration("test-24"), Duration.parse("PT25H1M1S"));
        assertEquals(configurations.getDuration("test-25"), Duration.parse("PT25H1M1S"));
        assertEquals(configurations.getDuration("test-26"), Duration.parse("PT25H1M1S"));

        assertEquals(configurations.getDuration("test-31"), Duration.parse("PT1H5M3.1S"));
        assertEquals(configurations.getDuration("test-32"), Duration.parse("PT2H35S"));
        assertEquals(configurations.getDuration("test-33"), Duration.parse("PT7M12S"));
        assertEquals(configurations.getDuration("test-34"), Duration.parse("PT3H"));
        assertEquals(configurations.getDuration("test-35"), Duration.parse("PT4M"));
        assertEquals(configurations.getDuration("test-36"), Duration.parse("PT59S"));
    }
}
