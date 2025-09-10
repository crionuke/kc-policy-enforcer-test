package com.omgservers.tenants.stage;

import com.omgservers.tenants.event.Event;
import com.omgservers.tenants.event.EventQualifier;
import com.omgservers.tenants.tenant.Tenant;
import com.omgservers.tenants.tenant.TenantResourceTest;
import com.omgservers.tenants.tenant.TenantStatus;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(StageResource.class)
public class StageResourceTest {

    @Inject
    TenantResourceTest tenantResourceTest;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Stage persistTestStage(final Tenant tenant) {
        final var testStage = createTestStage(tenant);
        testStage.persist();
        return testStage;
    }

    @Test
    void testGetStageByIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testStage = persistTestStage(testTenant);

        given()
                .pathParam("tenantId", testTenant.id)
                .pathParam("id", testStage.id)
                .when()
                .get("/{id}", testStage.id)
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testStage.id.intValue()))
                .body("tenant.id", equalTo(testTenant.id.intValue()))
                .body("name", equalTo(testStage.name))
                .body("status", equalTo(testStage.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetStageByIdNotFound() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var nonExistentId = new Random().nextLong();

        given()
                .pathParam("tenantId", testTenant.id)
                .pathParam("id", nonExistentId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .body("code", equalTo("StageNotFound"));
    }

    @Test
    void testCreateStageSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();

        final var newStage = new NewStage();
        newStage.name = "New stage";
        newStage.config = createStageConfig();

        final var stage = given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(newStage)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(newStage.name))
                .body("status", equalTo(StageStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Stage.class);

        Event.findFirstRequired(EventQualifier.STAGE_CREATED, stage.id);
    }

    @Test
    void testCreateStageValidationFailed() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var invalidStage = new NewStage();

        given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(invalidStage)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateStageTenantStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant(TenantStatus.CREATING);

        final var newStage = new NewStage();
        newStage.name = "New stage";
        newStage.config = createStageConfig();

        given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(newStage)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("TenantStatusMismatch"));
    }

    private Stage createTestStage(final Tenant tenant) {
        final var stage = new Stage();
        stage.tenant = tenant;
        stage.name = "Test stage";
        stage.status = StageStatus.CREATED;
        stage.config = createStageConfig();
        stage.persist();

        return stage;
    }

    private StageConfig createStageConfig() {
        final var config = new StageConfig();
        config.version = StageConfigVersion.V1;
        return config;
    }
}