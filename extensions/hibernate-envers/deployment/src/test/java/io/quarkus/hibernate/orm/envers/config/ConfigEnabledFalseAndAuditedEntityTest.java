package io.quarkus.hibernate.orm.envers.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.inject.Inject;
import javax.persistence.metamodel.Bindable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.hibernate.orm.envers.MyAuditedEntity;
import io.quarkus.test.QuarkusUnitTest;

public class ConfigEnabledFalseAndAuditedEntityTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar.addClass(MyAuditedEntity.class))
            .withConfigurationResource("application.properties")
            .overrideConfigKey("quarkus.hibernate-envers.enabled", "false");

    @Inject
    SessionFactory sessionFactory;

    @Test
    @SuppressWarnings("rawtypes")
    public void test() {
        assertThat(sessionFactory.getMetamodel().getEntities())
                .extracting(Bindable::getBindableJavaType)
                // In particular this should not contain the revision entity
                .containsExactlyInAnyOrder((Class) MyAuditedEntity.class);

        try (Session session = sessionFactory.openSession()) {
            assertThatThrownBy(() -> AuditReaderFactory.get(session).isEntityClassAudited(MyAuditedEntity.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Service is not yet initialized");
        }
    }
}
