package com.omgservers.tenants.project;

import com.omgservers.tenants.tenant.Tenant;
import com.omgservers.tenants.tenant.TenantResourceTest;
import com.omgservers.tenants.tenant.TenantStatus;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@ApplicationScoped
@TestHTTPEndpoint(ProjectResource.class)
public class ProjectResourceTest {

    @Inject
    TenantResourceTest tenantResourceTest;

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
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
                .when()
                .get("/{id}", testProject.id)
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testProject.id.toString()))
                .body("tenant.id", equalTo(testTenant.id.toString()))
                .body("name", equalTo(testProject.name))
                .body("status", equalTo(testProject.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetProjectByIdNotFound() {
        final var nonExistentId = UUID.randomUUID();

        given()
                .when()
                .get("/{id}", nonExistentId)
                .then()
                .statusCode(404)
                .body("code", equalTo("ProjectNotFound"));
    }

    @Test
    void testCreateProjectSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();

        final var newProject = new NewProject();
        newProject.tenantId = testTenant.id;
        newProject.name = "New project";
        newProject.config = createProjectConfig();

        given()
                .contentType(ContentType.JSON)
                .body(newProject)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(newProject.name))
                .body("status", equalTo(ProjectStatus.CREATING.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testCreateProjectValidationFailed() {
        final var invalidProject = new NewProject();

        given()
                .contentType(ContentType.JSON)
                .body(invalidProject)
                .when()
                .post()
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateProjectTenantStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant(TenantStatus.CREATING);

        final var newProject = new NewProject();
        newProject.tenantId = testTenant.id;
        newProject.name = "New project";
        newProject.config = createProjectConfig();

        given()
                .contentType(ContentType.JSON)
                .body(newProject)
                .when()
                .post()
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