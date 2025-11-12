package ukma.springboot.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ukma.springboot.nextskill.common.communication.ServiceNames;

@Configuration
public class GatewayConfig {

    @Value("${spring.activemq.broker-url-admin-panel}")
    private String brokerAdminPanelUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("eureka-service", r ->
                r.path("/admin/eureka/**")
                    .filters(f -> f.stripPrefix(2))
                    .uri("lb://" + ServiceNames.EUREKA_SERVER)
            )
            .route("config-service", r ->
                r.path("/admin/config/**")
                    .filters(f -> f.stripPrefix(2))
                    .uri("lb://" + ServiceNames.CONFIG_SERVICE)
            )
            .route("admin-service", r ->
                r.path("/admin/dashboard/**")
                    .filters(f -> f.stripPrefix(2))
                    .uri("lb://" + ServiceNames.ADMIN_SERVICE)
            )
            .route("admin-service", r ->
                r.path("/admin/broker/**")
                    .filters(f -> f.stripPrefix(2))
                    .uri(brokerAdminPanelUrl)
            )
            .route("webapp-service", r ->
                r.path("/app/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri("lb://" + ServiceNames.WEBAPP_SERVICE)
            )
            .build();
    }
}
