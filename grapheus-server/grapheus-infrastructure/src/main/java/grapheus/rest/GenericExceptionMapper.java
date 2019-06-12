/**
 * 
 */
package grapheus.rest;

import java.net.SocketException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.grapheus.client.model.RError;

import lombok.extern.slf4j.Slf4j;
import grapheus.persistence.exception.DocumentNotFoundException;
import grapheus.rest.resource.UnexpectedRemoteStatusCode;

/**
 * @author black
 *
 */
@Slf4j
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    
    public GenericExceptionMapper() {
        super();
    }

   // @Override
    public Response toResponse(Exception exception) {
        log.error("", exception);
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            return Response.fromResponse(wae.getResponse()). //
                    entity(RError.builder(). //
                            errorDescription(wae.getMessage()). //
                            build()). //
                    build();
        }
        
        
        
        return Response.status(getStatus(exception))
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    private int getStatus(Throwable exception) {
        if(exception instanceof DocumentNotFoundException) {
            return Status.NOT_FOUND.getStatusCode();
        } else if(exception instanceof UnexpectedRemoteStatusCode) {
            UnexpectedRemoteStatusCode urscException = (UnexpectedRemoteStatusCode) exception;
            return urscException.getStatus();
        } else {
            Throwable cause = exception.getCause();
            while(cause != null) {
                if(cause instanceof SocketException) {
                    log.error("Connection problem: {}", cause.getMessage());
                    return Status.SERVICE_UNAVAILABLE.getStatusCode();
                    
                }
                cause = cause.getCause();
            }
        }
        
        return Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

}
