/**
 * 
 */
package org.grapheus.client.http;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.model.telemetry.RTelemetryContainer;

import com.google.common.net.MediaType;

import lombok.AllArgsConstructor;

/**
 * @author black
 *
 */
public class GrapheusRestClientTest {
    @AllArgsConstructor
    private static class FailingCallback implements ExpectationResponseCallback {

        private int failuresCount;
        @Override
        public HttpResponse handle(HttpRequest httpRequest) {
            
            if(failuresCount == 0) {
                return HttpResponse.response()
                        .withStatusCode(200)
                        .withBody("{serversTelemetry:[]}", MediaType.JSON_UTF_8);
            }
            failuresCount--;
            return HttpResponse.response().withStatusCode(503);
        }
        
    }
    
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    
    @Test(expected = ServerErrorResponseException.class)
    public void testRetryFail() {
        mockServerClient
            .when(HttpRequest.request()
                .withMethod("GET"))
            .respond(new FailingCallback(4));
        GrapheusClientFactory cf = new GrapheusClientFactory("http:/"+mockServerClient.remoteAddress().toString());
        cf.telemetry().getTelemetry();
     
    }
    @Test
    public void testRetrySucceed() {
        mockServerClient
            .when(HttpRequest.request()
                .withMethod("GET"))
            .respond(new FailingCallback(3));
        GrapheusClientFactory cf = new GrapheusClientFactory("http:/"+mockServerClient.remoteAddress().toString());
        RTelemetryContainer telemetry = cf.telemetry().getTelemetry();
        Assert.assertNotNull(telemetry);
    }

}
