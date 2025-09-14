package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.Event;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.project.ProjectResourceTest;
import com.omgservers.omgservice.stage.Stage;
import com.omgservers.omgservice.stage.StageResourceTest;
import com.omgservers.omgservice.stage.StageStatus;
import com.omgservers.omgservice.tenant.TenantResourceTest;
import com.omgservers.omgservice.version.Version;
import com.omgservers.omgservice.version.VersionResourceTest;
import com.omgservers.omgservice.version.VersionStatus;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@ApplicationScoped
@TestSecurity(authorizationEnabled = false)
@TestHTTPEndpoint(DeploymentResource.class)
public class DeploymentResourceTest extends Assertions {

    @Inject
    TenantResourceTest tenantResourceTest;

    @Inject
    ProjectResourceTest projectResourceTest;

    @Inject
    VersionResourceTest versionResourceTest;

    @Inject
    StageResourceTest stageResourceTest;

    @Transactional
    public Deployment persistTestDeployment(final Stage stage, final Version version) {
        final var testDeployment = createTestDeployment(stage, version);
        testDeployment.persist();
        return testDeployment;
    }

    @Test
    void testGetDeploymentByIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = versionResourceTest.persistTestVersion(testProject);
        final var testStage = stageResourceTest.persistTestStage(testTenant);
        final var testDeployment = persistTestDeployment(testStage, testVersion);

        given()
                .pathParam("id", testDeployment.id)
                .when()
                .get("/deployment/{id}")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testDeployment.id.intValue()))
                .body("stage.id", equalTo(testStage.id.intValue()))
                .body("version.id", equalTo(testVersion.id.intValue()))
                .body("status", equalTo(testDeployment.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetDeploymentByIdNotFound() {
        final var nonExistentId = new Random().nextLong();

        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/deployment/{id}")
                .then()
                .log().body()
                .statusCode(404)
                .body("code", equalTo("DeploymentNotFound"));
    }

    @Test
    void testGetDeploymentByProjectIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();

        final var testStage1 = stageResourceTest.persistTestStage(testTenant);
        final var testStage2 = stageResourceTest.persistTestStage(testTenant);

        final var testProject1 = projectResourceTest.persistTestProject(testTenant);
        final var testVersion1 = versionResourceTest.persistTestVersion(testProject1);
        final var testVersion2 = versionResourceTest.persistTestVersion(testProject1);

        final var testProject2 = projectResourceTest.persistTestProject(testTenant);
        final var testVersion3 = versionResourceTest.persistTestVersion(testProject2);

        final var testDeployment1 = persistTestDeployment(testStage1, testVersion1);
        final var testDeployment2 = persistTestDeployment(testStage2, testVersion2);
        final var testDeployment3 = persistTestDeployment(testStage1, testVersion3);

        final var deployments = given()
                .pathParam("projectId", testProject1.id)
                .when()
                .get("/project/{projectId}/deployment")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Deployments.class);

        assertEquals(2, deployments.size);

        final var ids = deployments.list.stream().map(d -> d.id).toList();
        assertTrue(ids.contains(testDeployment1.id));
        assertTrue(ids.contains(testDeployment2.id));
        assertFalse(ids.contains(testDeployment3.id));
    }

    @Test
    void testCreateDeploymentSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = versionResourceTest.persistTestVersion(testProject);
        final var testStage = stageResourceTest.persistTestStage(testTenant);

        final var newDeployment = new NewDeployment();
        newDeployment.versionId = testVersion.id;

        final var deployment = given()
                .pathParam("stageId", testStage.id)
                .contentType(ContentType.JSON)
                .body(newDeployment)
                .when()
                .post("/stage/{stageId}/deployment")
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("stage.id", equalTo(testStage.id.intValue()))
                .body("version.id", equalTo(testVersion.id.intValue()))
                .body("status", equalTo(VersionStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Deployment.class);

        Event.findFirstRequired(EventQualifier.DEPLOYMENT_CREATED, deployment.id);
    }

    @Test
    void testCreateDeploymentValidationFailed() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = versionResourceTest.persistTestVersion(testProject);
        final var testStage = stageResourceTest.persistTestStage(testTenant);

        final var invalidDeployment = new NewDeployment();

        given()
                .pathParam("stageId", testStage.id)
                .contentType(ContentType.JSON)
                .body(invalidDeployment)
                .when()
                .post("/stage/{stageId}/deployment")
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateDeploymentStageStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = versionResourceTest.persistTestVersion(testProject);
        final var testStage = stageResourceTest.persistTestStage(testTenant, StageStatus.CREATING);

        final var newDeployment = new NewDeployment();
        newDeployment.versionId = testVersion.id;

        given()
                .pathParam("stageId", testStage.id)
                .contentType(ContentType.JSON)
                .body(newDeployment)
                .when()
                .post("/stage/{stageId}/deployment")
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("StageStatusMismatch"));
    }

    @Test
    void testCreateDeploymentVersionStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = versionResourceTest.persistTestVersion(testProject, VersionStatus.CREATING);
        final var testStage = stageResourceTest.persistTestStage(testTenant);

        final var newDeployment = new NewDeployment();
        newDeployment.versionId = testVersion.id;

        given()
                .pathParam("stageId", testStage.id)
                .contentType(ContentType.JSON)
                .body(newDeployment)
                .when()
                .post("/stage/{stageId}/deployment")
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("VersionStatusMismatch"));
    }

    private Deployment createTestDeployment(final Stage stage, final Version version) {
        final var deployment = new Deployment();
        deployment.stage = stage;
        deployment.version = version;
        deployment.status = DeploymentStatus.CREATED;
        deployment.config = new DeploymentConfig();
        deployment.config.version = DeploymentConfigVersion.V1;
        deployment.persist();

        return deployment;
    }
}