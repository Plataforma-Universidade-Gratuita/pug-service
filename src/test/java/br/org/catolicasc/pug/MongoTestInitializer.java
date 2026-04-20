package br.org.catolicasc.pug;

import br.org.catolicasc.pug.shared.infra.audit.AuditListener;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@Mock
@ApplicationScoped
public class MongoTestInitializer {

  @Inject AuditListener auditListener;

  public void onStart(@Observes StartupEvent ev) {
    auditListener.deleteAll();
  }
}
