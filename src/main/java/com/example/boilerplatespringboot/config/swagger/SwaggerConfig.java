package com.example.boilerplatespringboot.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.env}")
    private String serverEnv;
    @Value("${server.url}")
    private String serverUrl;
    final String BASE_PACKAGE = "com.example.boilerplatespringboot.api";
    final String ADMIN = "/admin";

    @Bean
    public GroupedOpenApi apiGroup() {

        // Use Reflections library to dynamically find sub-packages
        Reflections reflections = new Reflections(BASE_PACKAGE);

        // Convert Set to an array for `packagesToScan`
        // Extract package names
        String[] packagesArray = reflections.getTypesAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .stream()
            .map(clazz -> clazz.getPackage().getName())
            .distinct().toArray(String[]::new);

        return GroupedOpenApi.builder()
            .group("api") // Group name for Swagger UI
            .packagesToScan(packagesArray) // ✅ Supports package scanning!
            .pathsToExclude("/*" + ADMIN + "/**")
            .packagesToExclude(BASE_PACKAGE + ".error")
            .build();
    }

    @Bean
    public GroupedOpenApi apiGroupAdmin() {

        // Use Reflections library to dynamically find sub-packages
        Reflections reflections = new Reflections(BASE_PACKAGE);

        // Convert Set to an array for `packagesToScan`
        // Extract package names
        String[] packagesArray = reflections.getTypesAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .stream()
            .map(clazz -> clazz.getPackage().getName())
            .distinct().toArray(String[]::new);

        return GroupedOpenApi.builder()
            .group("admin-api") // Group name for Swagger UI
            .packagesToScan(packagesArray) // ✅ Supports package scanning!
            .pathsToMatch("/*" + ADMIN + "/**")
            .packagesToExclude(BASE_PACKAGE + ".error")
            .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components()
            .addSecuritySchemes("Authorization", new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.APIKEY)
                .description("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJodGoiLCJhdXRoIjoiIiwiZXhwIjoxNzQ5NTIyODg1fQ.HePVstmv7LV4mWWxCshyDWpo2JlEIfzvtPVAUWrSILk")
                .in(SecurityScheme.In.HEADER));

        return new OpenAPI()
            .components(components)
            .info(apiInfo())
            .servers(apiServer());
    }

    private Info apiInfo() {
        return new Info()
                .title("boilerplate")
                .description("<strong>prefix URI : /api</strong>")
                .version("1.0.0");
    }

    private List<Server> apiServer() {
        String description = "Local Server";
        if(serverEnv.equals("prod")) description = "Prod Server";

        Server server = new Server()
                .description(description)
                .url(serverUrl);

        return Collections.singletonList(server);
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            this.addResponseBodyWrapperSchemaExample(operation);
            return operation;
        };
    }

    private void addResponseBodyWrapperSchemaExample(Operation operation) {
        operation.getResponses().forEach((k, v) -> {
            final Content content = v.getContent();
            if (content != null) {
                content.forEach((mediaTypeKey, mediaType) -> {
                    Schema<?> originalSchema = mediaType.getSchema();
                    Schema<?> wrappedSchema = wrapSchema(originalSchema, k, v);
                    mediaType.setSchema(wrappedSchema);
                });
            }
        });
    }

    private Schema<?> wrapSchema(Schema<?> originalSchema, String key , ApiResponse res) {
        final Schema<?> wrapperSchema = new Schema<>();

        wrapperSchema.addProperty("status", new Schema<>().type("integer").example(key));
        wrapperSchema.addProperty("message", new Schema<>().type("string").example(res.getDescription()));
        wrapperSchema.addProperty("timestamp", new Schema<>().type("string").example(res.getDescription()));
        if ("200".equals(key)) {
            wrapperSchema.addProperty("data", originalSchema);
        }

        return wrapperSchema;
    }
}
