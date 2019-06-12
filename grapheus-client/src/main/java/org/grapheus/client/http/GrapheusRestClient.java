/**
 * 
 */
package org.grapheus.client.http;

import static java.lang.String.format;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.grapheus.client.http.auth.GrapheusClientCredentials;
import org.grapheus.client.model.RError;
import org.grapheus.common.rest.GsonContextResolver;
import org.zalando.jersey.gson.GsonFeature;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;


/**
 * NOTE: not thread-friendly
 * @author black
 *
 */
@Slf4j
public class GrapheusRestClient {
    private final RetryPolicy retryPolicy = new RetryPolicy()
            .retryIf((Response r)->r.getStatus() == 503)
            .withDelay(1, TimeUnit.SECONDS)
            .withMaxRetries(3);
    
    @Getter
    private final String baseURL;
    private final Client client;

    public GrapheusRestClient(String baseURL, Supplier<GrapheusClientCredentials> credsSupplier) {
        this.baseURL = baseURL;
  
        ClientBuilder clientBuilder = ClientBuilder.newBuilder().//
                register(new AuthorizationFilter(credsSupplier)).//
                register(GsonContextResolver.class).//
                register(GsonFeature.class).
                property(ClientProperties.CONNECT_TIMEOUT, 20000).
                property(ClientProperties.READ_TIMEOUT,    20000);
        if(log.isTraceEnabled()) {
            clientBuilder = clientBuilder
                    .property(LoggingFeature.LOGGING_FEATURE_LOGGER_NAME_CLIENT, GrapheusRestClient.class.getName())
                    .property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, Level.INFO.getName())
                    .register(LoggingFeature.class);
        }
        client = clientBuilder.build();
    }

    public <T> T post(String resourcePath, Object entity, Class<T> responseEntityClass) throws ServerErrorResponseException {
        log.debug("POSTing to {}", resourcePath);
        Response r = request(resourcePath).post(Entity.entity(entity, MediaType.APPLICATION_JSON));

        assertStatus(Status.CREATED, r);

        return r.readEntity(responseEntityClass);
    }

    public void post(URI resourcePath, InputStream is, String mediaType) throws ServerErrorResponseException {
        log.debug("POSTing {} to {}", mediaType, resourcePath);
        Response r = request(resourcePath.toString()).post(Entity.entity(is, mediaType));
        assertStatus(Status.CREATED, r);
    }

    public void post(String resourcePath, Object entity) throws ServerErrorResponseException {
        log.debug("POSTing to {}", resourcePath);
        Response r = request(resourcePath).post(Entity.entity(entity, MediaType.APPLICATION_JSON));
        assertStatus(Status.CREATED, r);
    }
    public void post(URI resourcePath, Object entity) throws ServerErrorResponseException {
        post(resourcePath.toString(), entity);
    }

    public void put(URI resourcePath, Object entity) {
        put(resourcePath.toString(), entity);
    }
    
    @Deprecated
    public void put(String resourcePath, Object entity) {
        log.debug("PUTing to {}", resourcePath);
        Response r = request(resourcePath).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
        assertStatus(Status.NO_CONTENT, r);
    }
    
    public void patch(URI resourcePath, Object entity) {
        log.debug("PATCHing to {}", resourcePath);
        Response r = request(resourcePath.toString()).method("PATCH", Entity.entity(entity, MediaType.APPLICATION_JSON));
        assertStatus(Status.NO_CONTENT, r);
    }

    public <T> T get(String resourcePath, Class<T> entityClass, Map<String, Object> params) throws ServerErrorResponseException {
        log.debug("GETing from {}", resourcePath);
        Builder reqBuilder = request(resourcePath, params);
        Response r =  Failsafe.with(retryPolicy).get(()->reqBuilder.get());
        assertStatus(Status.OK, r);
        return r.readEntity(entityClass);
    }
    
    public <T> T get(URI resourcePathURI, Class<T> entityClass) throws ServerErrorResponseException {
        return get(resourcePathURI.toString(), entityClass, Collections.emptyMap());
    }
    
    public <T> T get(String resourcePath, Class<T> entityClass) throws ServerErrorResponseException {
        return get(resourcePath, entityClass, Collections.emptyMap());
    }

    @Deprecated
    public void delete(String resourcePath) {
        log.debug("DELETing {}", resourcePath);
        Response r = request(resourcePath).delete();
        assertStatus(Status.NO_CONTENT, r);
    }
    
    public void delete(URI uri) {
        this.delete(uri.toString());
    }

    private static void assertStatus(Status expectedStatus, Response r) {
        if (expectedStatus.getStatusCode() != r.getStatus()) {
            if(r.getStatus() >= 400) {
                RError error;
                try {
                    error = r.readEntity(RError.class);
                } catch (Exception e) {
                    throw new ServerErrorResponseException(r.getStatus());
                }
                throw new ServerErrorResponseException(
                        Optional.ofNullable(error).map(RError::getErrorDescription).orElse("???"),
                        r.getStatus());
            } else {
                log.error("Error response code={}, content={}", r.getStatus(), r.readEntity(String.class));
                throw new ServerErrorResponseException(r.getStatus());
            } 
        }
    }

    private Builder request(String resourcePath) {
        return request(resourcePath, Collections.emptyMap());
    }

    private Builder request(String resourcePath, Map<String, Object> queryParams) {
        WebTarget target = client.target(resourceURL(resourcePath));
        for(Map.Entry<String, Object> entry: queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }
        Builder request = target.request(MediaType.APPLICATION_JSON_TYPE);
        return request;
    }

    private String resourceURL(String resourcePath) {
        return format("%s/%s", baseURL, resourcePath);
    }


    public Builder getRequestBuilder(String resourcePath, Map<String, Object> queryParams) {
        return request(resourcePath, queryParams);
    }

    public <T> T post(URI uri, Object payload, Class<T> returnClass) {
        return post(uri.toString(), payload, returnClass);
    }

}
