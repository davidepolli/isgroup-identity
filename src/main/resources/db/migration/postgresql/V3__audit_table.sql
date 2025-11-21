CREATE TABLE IF NOT EXISTS audit_log (
  id           BIGSERIAL PRIMARY KEY,
  occurred_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actor        VARCHAR(100) NOT NULL,
  action       VARCHAR(60)  NOT NULL,
  resource     VARCHAR(60)  NOT NULL,
  resource_id  VARCHAR(60),
  outcome      VARCHAR(20)  NOT NULL,
  details      VARCHAR(4000)
);

CREATE INDEX IF NOT EXISTS idx_audit_log_when  ON audit_log(occurred_at);
CREATE INDEX IF NOT EXISTS idx_audit_log_actor ON audit_log(actor);