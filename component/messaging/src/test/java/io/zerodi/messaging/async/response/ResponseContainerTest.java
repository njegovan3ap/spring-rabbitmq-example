package io.zerodi.messaging.async.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.zerodi.messaging.core.UniqueId;
import io.zerodi.messaging.core.UniqueIdFactory;

@RunWith(MockitoJUnitRunner.class)
public class ResponseContainerTest {

    @InjectMocks
    private ResponseContainer responseContainer;

    @Spy
    private UniqueIdFactory uniqueIdFactory;

    private UniqueId uniqueId;

    @Before
    public void setUp() throws Exception {
        uniqueId = uniqueIdFactory.createUniqueId();
    }

    @Test(expected = NullPointerException.class)
    public void itShouldNotAcceptNullId() throws Exception {
        // when
        responseContainer.awaitResponse(null);
    }

    @Test
    public void itShouldProduceFuturesWhichArePending() throws Exception {
        // when
        Future<Object> response = responseContainer.awaitResponse(uniqueId);

        // then
        assertThat(response.isDone()).isFalse();
    }

    @Test
    public void itShouldAllowWaitingForTheResponse() throws Exception {
        // when
        Future<Object> response = responseContainer.awaitResponse(uniqueId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    public void itShouldAllowNotifyingPendingFuture() throws Exception {
        // given
        Future<String> response = responseContainer.awaitResponse(uniqueId);
        String exampleResponse = "example";

        // when
        responseContainer.notifyWithResponse(uniqueId, exampleResponse);

        // then
        assertThat(response.isDone()).isTrue();
    }

    @Test
    public void itShouldAllowPassingExceptionToPendingFuture() throws Exception {
        // given
        Future<String> response = responseContainer.awaitResponse(uniqueId);
        RuntimeException exceptionToBeThrown = new RuntimeException("example");

        // when
        responseContainer.notifyWithError(uniqueId, exceptionToBeThrown);

        // then
        assertThat(response.isDone()).isTrue();
        assertThat(response.isCancelled()).isFalse();

        try {
            response.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            assertThat(cause).isEqualTo(exceptionToBeThrown);
            return;
        }

        fail("an exception should have been thrown");
    }

    @Test(expected = NullPointerException.class)
    public void itShouldNotAcceptNullIdWhenPassingResponse() throws Exception {
        // when
        responseContainer.notifyWithResponse(null, new Object());
    }

    @Test(expected = NullPointerException.class)
    public void itShouldNotAcceptNullIdWhenPassingException() throws Exception {
        // when
        responseContainer.notifyWithError(null, new RuntimeException());
    }

    @Test(expected = NullPointerException.class)
    public void itShouldNotAcceptNullException() throws Exception {
        // when
        responseContainer.notifyWithError(uniqueIdFactory.createUniqueId(), null);
    }


}
