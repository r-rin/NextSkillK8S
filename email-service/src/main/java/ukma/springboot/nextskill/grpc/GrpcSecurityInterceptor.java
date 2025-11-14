package ukma.springboot.nextskill.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GrpcGlobalServerInterceptor
public class GrpcSecurityInterceptor implements ServerInterceptor {

    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    // In production, this should be a proper JWT validation with secret key
    private static final String VALID_TOKEN = "nextskill-secure-token-2025";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(Metadata.Key.of(AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER));

        log.debug("Intercepting gRPC call: {} with auth header: {}", call.getMethodDescriptor().getFullMethodName(), authHeader);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid authorization header");
            call.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid authorization header"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!isValidToken(token)) {
            log.warn("Invalid token provided");
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid authentication token"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        log.debug("Authentication successful for call: {}", call.getMethodDescriptor().getFullMethodName());
        return next.startCall(call, headers);
    }

    private boolean isValidToken(String token) {
        // In production, implement proper JWT validation:
        // - Verify signature
        // - Check expiration
        // - Validate issuer
        // - Check claims
        
        // For demo purposes, simple token comparison
        return VALID_TOKEN.equals(token);
    }
}
