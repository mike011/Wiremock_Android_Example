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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity>
{

    private static final String WIRE_MOCK_IP = "192.168.0.100";
    private static final int WIRE_MOCK_PORT = 8080;

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

    @Test
    public void testStartActivity() throws Exception
    {
        String hello = this.getActivity().getString(R.string.hello_world);
        assertThat(hello, equalTo("Hello world!"));
    }

    @Test
    public void testStartActivityEspresso() throws Exception
    {
        onView(withId(R.id.helloWorld)).check(matches(withText(R.string.hello_world)));
    }

    private static void stubServerCall() throws Exception
    {
        WireMock.configureFor(WIRE_MOCK_IP, WIRE_MOCK_PORT);
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