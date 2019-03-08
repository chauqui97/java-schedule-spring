package com.alimiportal.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket productApi() {
		List<ResponseMessage> responseMessageList = new ArrayList<>();
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.OK.value())
				.message(HttpStatus.OK.getReasonPhrase()).build());
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.BAD_REQUEST.value())
				.message(HttpStatus.BAD_REQUEST.getReasonPhrase()).build());
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.UNAUTHORIZED.value())
				.message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build());
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.FORBIDDEN.value())
				.message(HttpStatus.FORBIDDEN.getReasonPhrase()).build());
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value())
				.message(HttpStatus.NOT_FOUND.getReasonPhrase()).build());
		responseMessageList.add(new ResponseMessageBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).build());

		List<Parameter> parameterList = new ArrayList<>();
		parameterList.add(new ParameterBuilder().name("Authorization")
				.description(
						"Authorization header containing the returned token when login using authentication-service")
				.modelRef(new ModelRef("string")).parameterType("header").build());

		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().apiInfo(apiInfo())
				.globalResponseMessage(RequestMethod.GET, responseMessageList).globalOperationParameters(parameterList);
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Data Ingestion Service", "This is Data Ingestion ", "1.0", "TERMS OF SERVICE URL",
				new Contact("name", "web", "email"), "LICENSE", "LICENSE URL", Collections.emptyList());
	}
}
