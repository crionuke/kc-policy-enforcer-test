package com.omgservers.omgservice.project;

import com.omgservers.omgservice.event.Event;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.tenant.Tenant;
import com.omgservers.omgservice.tenant.TenantResourceTest;
import com.omgservers.omgservice.tenant.TenantStatus;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@ApplicationScoped
@TestHTTPEndpoint(ProjectResource.class)
public class ProjectResourceTest {

    @Inject
    TenantResourceTest tenantResourceTest;

    @Transactional
    public Project persistTestProject(final Tenant tenant, final ProjectStatus status) {
        final var testProject = createTestProject(tenant, status);
        testProject.persist();
        return testProject;
    }

    public Project persistTestProject(final Tenant tenant) {
        return persistTestProject(tenant, ProjectStatus.CREATED);
    }

    @Test
    void testGetProjectByIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = persistTestProject(testTenant);

        given()
                .pathParam("id", testProject.id)
                .when()
                .get("/project/{id}")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testProject.id.intValue()))
                .body("tenant.id", equalTo(testTenant.id.intValue()))
                .body("name", equalTo(testProject.name))
                .body("status", equalTo(testProject.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetProjectByIdNotFound() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var nonExistentId = new Random().nextLong();

        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/project/{id}")
                .then()
                .statusCode(404)
                .body("code", equalTo("ProjectNotFound"));
    }

    @Test
    void testCreateProjectSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();

        final var newProject = new NewProject();
        newProject.name = "New project";
        newProject.config = createProjectConfig();

        final var project = given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(newProject)
                .when()
                .post("/tenant/{tenantId}/project")
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(newProject.name))
                .body("status", equalTo(ProjectStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Project.class);

        Event.findFirstRequired(EventQualifier.PROJECT_CREATED, project.id);
    }

    @Test
    void testCreateProjectValidationFailed() {
        final var testTenant = tenantResourceTest.persistTestTenant();

        final var invalidProject = new NewProject();

        given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(invalidProject)
                .when()
                .post("/tenant/{tenantId}/project")
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateProjectTenantStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant(TenantStatus.CREATING);

        final var newProject = new NewProject();
        newProject.name = "New project";
        newProject.config = createProjectConfig();

        given()
                .pathParam("tenantId", testTenant.id)
                .contentType(ContentType.JSON)
                .body(newProject)
                .when()
                .post("/tenant/{tenantId}/project")
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("TenantStatusMismatch"));
    }

    private Project createTestProject(final Tenant tenant, final ProjectStatus status) {
        final var project = new Project();
        project.tenant = tenant;
        project.name = "Test project";
        project.status = status;
        project.config = createProjectConfig();
        project.persist();

        return project;
    }

    private ProjectConfig createProjectConfig() {
        final var config = new ProjectConfig();
        config.version = ProjectConfigVersion.V1;
        return config;
    }
}