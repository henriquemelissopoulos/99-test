package com.henriquemelissopoulos.igot99problemsbutanappaintone;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.henriquemelissopoulos.igot99problemsbutanappaintone.view.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by h on 02/12/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        sleep(5000);
    }

    @Test
    public void hasMap() {
        MainActivity activity = activityRule.getActivity();

        assertNotNull(activity);
        assertNull(activity.findViewById(0));

        assertNotNull(activity.getSupportFragmentManager().findFragmentById(R.id.map));
    }

    @Test
    public void shouldRefreshList() {
        onView(withId(R.id.fabRefresh)).perform(click());
        SystemClock.sleep(3000);
    }
}
