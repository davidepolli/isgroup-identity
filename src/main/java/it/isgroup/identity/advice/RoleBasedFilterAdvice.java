package it.isgroup.identity.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@ControllerAdvice
public class RoleBasedFilterAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter rt, Class<? extends HttpMessageConverter<?>> conv) {
		return org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.class.isAssignableFrom(conv);
	}

	@Override
	public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
			@NonNull MediaType contentType, @NonNull Class<? extends HttpMessageConverter<?>> converterType,
			@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

		if (body == null)
			return null;

		SimpleFilterProvider filters = new com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider().setFailOnUnknownId(false)
				.addFilter("userFilter", filterFor(currentAuth()));

		if (body instanceof org.springframework.http.converter.json.MappingJacksonValue mjv) {
			mjv.setFilters(filters);
			return mjv;
		}

		var wrapper = new org.springframework.http.converter.json.MappingJacksonValue(body);
		wrapper.setFilters(filters);
		return wrapper;
	}

	private SimpleBeanPropertyFilter filterFor(Authentication auth) {
		if (hasRole(auth, "ADMIN")) {
			return SimpleBeanPropertyFilter.serializeAll();
		} else if (hasRole(auth, "OPERATOR")) {
			return SimpleBeanPropertyFilter.serializeAllExcept("taxCode");
		} else {
			return SimpleBeanPropertyFilter.serializeAllExcept("taxCode", "roles");
		}
	}

	private boolean hasRole(Authentication auth, String role) {
		return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
	}

	private Authentication currentAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
}
