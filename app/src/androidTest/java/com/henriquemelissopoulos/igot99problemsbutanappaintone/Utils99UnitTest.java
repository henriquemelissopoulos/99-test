package com.henriquemelissopoulos.igot99problemsbutanappaintone;

import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.utils.Utils99;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by h on 02/12/15.
 */
public class Utils99UnitTest {

    Double lat, lon;

    @Before
    public void setUp() {
        lat = 23.543514410323613;
        lon = -46.46779663860798;
    }

    @Test
    public void utils_toLatLon_LatitudeValidation() {
        assertThat(Utils99.toLatLng(lat, lon).latitude, is(23.543514410323613));
        assertThat(Utils99.toLatLng(lat, lon).latitude, not(-23.543514410323613));
        assertThat(Utils99.toLatLng(lat, lon).latitude, not(-46.46779663860798));
        assertThat(Utils99.toLatLng(lat, lon).latitude, not(0.0));
    }


    @Test
    public void utils_toLatLon_LongitudeValidation() {
        assertThat(Utils99.toLatLng(lat, lon).longitude, is(-46.46779663860798));
        assertThat(Utils99.toLatLng(lat, lon).longitude, not(46.46779663860798));
        assertThat(Utils99.toLatLng(lat, lon).longitude, not(23.543514410323613));
        assertThat(Utils99.toLatLng(lat, lon).longitude, not(0.0));
    }

}
