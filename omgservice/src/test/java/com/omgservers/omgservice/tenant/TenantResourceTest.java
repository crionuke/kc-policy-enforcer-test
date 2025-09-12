package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.event.Event;
import com.omgservers.omgservice.event.EventQualifier;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@ApplicationScoped
@TestSecurity(authorizationEnabled = false)
@TestHTTPEndpoint(TenantResource.class)
public class TenantResourceTest {

    @Transactional
    public Tenant persistTestTenant(final TenantStatus status) {
        final var testTenant = createTestTenant(status);
        testTenant.persist();
        return testTenant;
    }

    @Transactional
    public Tenant persistTestTenant() {
        return persistTestTenant(TenantStatus.CREATED);
    }

    @Test
    void testGetTenantByIdSuccess() {
        final var testTenant = persistTestTenant();

        given()
                .pathParam("id", testTenant.id)
                .when()
                .get("/tenant/{id}")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTenant.id.intValue()))
                .body("name", equalTo(testTenant.name))
                .body("status", equalTo(testTenant.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetTenantByIdNotFound() {
        final var nonExistentId = new Random().nextLong();

        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/tenant/{id}")
                .then()
                .statusCode(404)
                .body("code", equalTo("TenantNotFound"));
    }

    @Test
    void testCreateTenantSuccess() {
        final var newTenant = new NewTenant();
        newTenant.name = "New tenant";
        newTenant.config = createTenantConfig();

        final var tenant = given()
                .contentType(ContentType.JSON)
                .body(newTenant)
                .when()
                .post("/tenant")
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(newTenant.name))
                .body("status", equalTo(TenantStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Tenant.class);

        Event.findFirstRequired(EventQualifier.TENANT_CREATED, tenant.id);
    }

    @Test
    void testCreateTenantValidationFailed() {
        final var invalidTenant = new NewTenant();

        given()
                .contentType(ContentType.JSON)
                .body(invalidTenant)
                .when()
                .post("/tenant")
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    private Tenant createTestTenant(final TenantStatus status) {
        final var tenant = new Tenant();
        tenant.name = "Test tenant";
        tenant.status = status;
        tenant.config = createTenantConfig();
        tenant.persist();

        return tenant;
    }

    private TenantConfig createTenantConfig() {
        final var config = new TenantConfig();
        config.version = TenantConfigVersion.V1;
        return config;
    }
}