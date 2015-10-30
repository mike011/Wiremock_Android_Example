package com.example.myprojectwithwiremock;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

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

    private static void stubServer() throws Exception
    {
        WireMock.configureFor("192.168.0.100", 8080);
        stubFor(get(urlEqualTo("/seam/resource/rest/recipe/list"))
                //.withHeader("Accept", equalTo("text/xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>This is mock response</response>")));


    }

    @Test
    public void testSendRequestEspresso() throws Exception
    {
        stubServer();
        TextView returnStringLbl = (TextView) this.getActivity().findViewById(R.id.returnStringLbl);

        onView(withId(R.id.returnStringLbl)).check(matches(withText("Nothing yet returned")));

        Button sendRequestBtn = (Button) this.getActivity().findViewById(R.id.sendRequest);
        onView(withId(R.id.sendRequest)).perform(click());
        onView(withId(R.id.returnStringLbl)).check(matches(not(withText("Nothing yet returned"))));
        onView(withId(R.id.returnStringLbl)).check(matches(withText("<response>This is mock response</response>")));
    }

    protected void checkToast(String s)
    {
        onView(withText(s)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}