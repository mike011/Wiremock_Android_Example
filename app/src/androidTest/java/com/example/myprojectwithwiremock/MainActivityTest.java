package com.example.myprojectwithwiremock;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.HttpStatus;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity>
{

    public MainActivityTest()
    {
        super(MainActivity.class);
    }

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
    }

    private static void stubServerCall() throws Exception
    {
        WireMock.configureFor(BuildConfig.IP, BuildConfig.PORT);
        stubFor(get(urlEqualTo("/seam/resource/rest/recipe/list"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.ORDINAL_200_OK)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>This is mock response</response>")));
    }

    @Test
    public void testSendRequestEspresso() throws Exception
    {
        stubServerCall();
        onView(withId(R.id.returnStringLbl)).check(matches(withText("Nothing yet returned")));
        onView(withId(R.id.sendRequest)).perform(click());
        onView(withId(R.id.returnStringLbl)).check(matches(not(withText("Nothing yet returned"))));
        onView(withId(R.id.returnStringLbl)).check(matches(withText("<response>This is mock response</response>")));
    }
}