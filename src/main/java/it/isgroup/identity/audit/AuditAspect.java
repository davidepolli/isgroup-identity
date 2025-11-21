package it.isgroup.identity.audit;

import java.time.Instant;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import it.isgroup.identity.jpa.entities.AuditLog;
import it.isgroup.identity.jpa.repository.AuditLogRepository;

@Aspect
@Component
public class AuditAspect {
	private final AuditLogRepository auditRepo;

	public AuditAspect(AuditLogRepository auditRepo) {
		this.auditRepo = auditRepo;
	}

	@Around("@annotation(audit)")
	public Object around(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
		String actor = currentActor();
		try {
			Object result = pjp.proceed();
			auditRepo.save(new AuditLog(/* occurredAt */ Instant.now(), /* actor */ actor, /* action */ audit.action(),
					/* resource */ audit.resource(), /* resourceId */ resolveId(pjp, audit.idArg()),
					/* outcome */ "SUCCESS", /* details */ null));
			return result;
		} catch (Throwable t) {
			auditRepo.save(
					new AuditLog(
							Instant.now(),
							actor,
							audit.action(),
							audit.resource(),
							resolveId(pjp, audit.idArg()),
							"ERROR", t.getClass().getSimpleName() + ": " + t.getMessage())
					);
			throw t;
		}
	}

	private String resolveId(ProceedingJoinPoint pjp, String idArg) {
		if (idArg == null || idArg.isBlank())
			return null;
		MethodSignature sig = (MethodSignature) pjp.getSignature();
		String[] names = sig.getParameterNames();
		Object[] args = pjp.getArgs();
		for (int i = 0; i < names.length; i++) {
			if (idArg.equals(names[i]) && args[i] != null)
				return String.valueOf(args[i]);
		}
		return null;
	}

	private String currentActor() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null || !a.isAuthenticated())
			return "anonymous";

		// OAuth2 login (Authorization Code): principal is an OIDC user
		if (a instanceof OAuth2AuthenticationToken o) {
			Map<String, Object> attrs = o.getPrincipal().getAttributes();
			Object uname = attrs.getOrDefault("preferred_username", attrs.getOrDefault("email", o.getName()));
			return String.valueOf(uname);
		}

		// Resource server / JWT
		if (a instanceof JwtAuthenticationToken j) {
			Map<String, Object> c = j.getToken().getClaims();
			// prefer human username/email if present
			String user = (String) c.getOrDefault("preferred_username", (String) c.get("email"));
			if (user != null)
				return user;
			// otherwise identify the calling client
			String client = (String) c.getOrDefault("azp", c.get("client_id"));
			if (client != null)
				return "client:" + client;
		}

		// Mock users in tests (UsernamePasswordAuthenticationToken) or other auth types
		return a.getName();
	}
}
