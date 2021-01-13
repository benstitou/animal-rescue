package io.spring.cloud.samples.animalrescue.backend;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("oauth2")
public class OAuth2SecurityConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(OAuth2SecurityConfiguration.class);

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		httpSecurity.oauth2ResourceServer()
		            .jwt()
					.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(new UserNameJwtAuthenticationConverter()));

		return httpSecurity.build();
	}

	private static class UserNameJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

		private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
			= new JwtGrantedAuthoritiesConverter();

		@Override
		public AbstractAuthenticationToken convert(Jwt jwt) {
			Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
			return new JwtAuthenticationToken(jwt, authorities, getUserName(jwt));
		}

		private String getUserName(Jwt jwt) {
			return jwt.containsClaim("user_name") ? jwt.getClaimAsString("user_name") : jwt.getSubject();
		}
	}
}
