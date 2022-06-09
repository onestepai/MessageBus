package com.onestep.os.messagebusservice.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
  public static String serviceVersion = "1.0";
  @Value("${proxy.base_path}")
  private String proxyPath;
  @Value("${proxy.host_path}")
  private String hostPath;

  @Bean
  public Docket api(ServletContext servletContext) {
    Docket docket =
        new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(
                RequestHandlerSelectors.basePackage("com.onestep.os.messagebusservice.controller"))
            .build();
    if (!StringUtils.isEmpty(hostPath)) {
      docket.host(hostPath);
    }
    if (!StringUtils.isEmpty(proxyPath)) {
      if (!StringUtils.isEmpty(hostPath)) {
        docket.host(hostPath + proxyPath);
      } else {
        docket.pathProvider(
            new RelativePathProvider(servletContext) {
              @Override
              public String getApplicationBasePath() {
                return super.getApplicationBasePath() + proxyPath;
              }
            });
      }
    }

    ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
    List<ApiKey> apiKeyList = Lists.newArrayList(apiKey);
    docket.securitySchemes(apiKeyList);

    AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] scopes = new AuthorizationScope[] {scope};
    SecurityReference reference = new SecurityReference("Authorization", scopes);
    List<SecurityReference> referenceList = Lists.newArrayList(reference);
    SecurityContext securityContext =
        SecurityContext.builder().securityReferences(referenceList).build();
    List<SecurityContext> securityContextList = Lists.newArrayList(securityContext);
    docket.securityContexts(securityContextList);
    return docket;
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Os Message Bus service")
        .description("Os Message Bus service api")
        .version(serviceVersion)
        .build();
  }

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
