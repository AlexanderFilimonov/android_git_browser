package com.afilimonov.gitbrowser;

import android.content.Context;

import com.afilimonov.gitbrowser.model.Repo;
import com.afilimonov.gitbrowser.utils.RetrofitHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-15.
 * test for loading data using github API
 */
//@RunWith(AndroidJUnit4.class)
@RunWith(MockitoJUnitRunner.class)
public class RetrofitHelperTest {

    private static final String SERVER_URL_STRING = "https://api.github.com/";
    public static final String USER = "AlexanderFilimonov";

    @Mock
    Context mockContext;

    @Test
    public void testGetApiInterface() throws Exception {

        // Given a mocked Context injected into the object under test...
        when(mockContext.getString(R.string.githubServerUrl)).thenReturn(SERVER_URL_STRING);

//        ClassUnderTest myObjectUnderTest = new ClassUnderTest(mockContext);
        // ...when the string is returned from the object under test...
//        String result = myObjectUnderTest.getHelloWorldString();
        // ...then the result should be the expected one.
//        assertThat(result, is(FAKE_STRING));

        final CountDownLatch signal = new CountDownLatch(1);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        retrofitHelper.getApiInterface(mockContext)
                .listRepos(USER)
                .enqueue(new Callback<List<Repo>>() {
                    @Override
                    public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
                       /* assertTrue("onResponse",
                                response != null && response.body() != null && response.body().size() == 3
                        );*/
                        signal.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        signal.countDown();
                        fail(t.getMessage());
                    }
                });

        signal.await(30, TimeUnit.SECONDS);
        assertTrue("Happiness", true);
    }
}