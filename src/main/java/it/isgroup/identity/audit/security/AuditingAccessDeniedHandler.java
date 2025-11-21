package it.isgroup.identity.audit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import it.isgroup.identity.jpa.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TODO audit di eventuali access denied in quanto avvengono prima che
 * la chiamata arrivi al controller
 */
public class AuditingAccessDeniedHandler implements AccessDeniedHandler {	

	private static final Logger LOG = LoggerFactory.getLogger(AuditingAccessDeniedHandler.class);
	
	private final AuditLogRepository repo;

	  public AuditingAccessDeniedHandler(AuditLogRepository repo) { this.repo = repo; }

	  @Override
	  public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) {
		  
		  if(LOG.isWarnEnabled()) {
			  LOG.warn("[handle] access denied for request {}",  req.getRequestURI());
		  }
		  
	  }
}
