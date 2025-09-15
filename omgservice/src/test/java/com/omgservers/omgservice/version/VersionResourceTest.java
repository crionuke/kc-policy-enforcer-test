package com.omgservers.omgservice.version;

import com.omgservers.omgservice.OidcClients;
import com.omgservers.omgservice.event.Event;
import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.project.Project;
import com.omgservers.omgservice.project.ProjectResourceTest;
import com.omgservers.omgservice.project.ProjectStatus;
import com.omgservers.omgservice.tenant.TenantResourceTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
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
@TestSecurity(authorizationEnabled = false)
@TestHTTPEndpoint(VersionResource.class)
public class VersionResourceTest {

    @Inject
    OidcClients oidcClients;

    @Inject
    TenantResourceTest tenantResourceTest;

    @Inject
    ProjectResourceTest projectResourceTest;

    @Transactional
    public Version persistTestVersion(final Project project, VersionStatus status) {
        final var testVersion = createTestVersion(project, status);
        testVersion.persist();
        return testVersion;
    }

    public Version persistTestVersion(final Project project) {
        return persistTestVersion(project, VersionStatus.CREATED);
    }

    @Test
    void testGetVersionByIdSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var testVersion = persistTestVersion(testProject);

        given()
                .pathParam("id", testVersion.id)
                .when()
                .get("/version/{id}")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testVersion.id.intValue()))
                .body("project.id", equalTo(testProject.id.intValue()))
                .body("major", equalTo(testVersion.major.intValue()))
                .body("minor", equalTo(testVersion.minor.intValue()))
                .body("patch", equalTo(testVersion.patch.intValue()))
                .body("status", equalTo(testVersion.status.toString()))
                .body("config", notNullValue());
    }

    @Test
    void testGetVersionByIdNotFound() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);
        final var nonExistentId = new Random().nextLong();

        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/version/{id}")
                .then()
                .log().body()
                .statusCode(404)
                .body("code", equalTo("VersionNotFound"));
    }

    @Test
    void testCreateVersionSuccess() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);

        final var newVersion = new NewVersion();
        newVersion.major = 1L;
        newVersion.minor = 0L;
        newVersion.patch = 0L;

        final var version = given()
                .auth().oauth2(oidcClients.getAdminAccessToken())
                .pathParam("projectId", testProject.id)
                .contentType(ContentType.JSON)
                .body(newVersion)
                .when()
                .post("/project/{projectId}/version")
                .then()
                .log().body()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("major", equalTo(newVersion.major.intValue()))
                .body("minor", equalTo(newVersion.minor.intValue()))
                .body("patch", equalTo(newVersion.patch.intValue()))
                .body("status", equalTo(VersionStatus.CREATING.toString()))
                .body("config", notNullValue())
                .extract().as(Version.class);

        Event.findFirstRequired(EventQualifier.VERSION_CREATED, version.id);
    }

    @Test
    void testCreateVersionValidationFailed() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant);

        final var invalidVersion = new NewVersion();

        given()
                .pathParam("projectId", testProject.id)
                .contentType(ContentType.JSON)
                .body(invalidVersion)
                .when()
                .post("/project/{projectId}/version")
                .then()
                .log().body()
                .statusCode(400)
                .body("code", equalTo("ValidationFailed"));
    }

    @Test
    void testCreateVersionProjectStatusMismatch() {
        final var testTenant = tenantResourceTest.persistTestTenant();
        final var testProject = projectResourceTest.persistTestProject(testTenant, ProjectStatus.CREATING);

        final var newVersion = new NewVersion();
        newVersion.major = 1L;
        newVersion.minor = 0L;
        newVersion.patch = 0L;

        given()
                .pathParam("projectId", testProject.id)
                .contentType(ContentType.JSON)
                .body(newVersion)
                .when()
                .post("/project/{projectId}/version")
                .then()
                .log().body()
                .statusCode(409)
                .body("code", equalTo("ProjectStatusMismatch"));
    }

    private Version createTestVersion(final Project project, final VersionStatus status) {
        final var version = new Version();
        version.project = project;
        version.major = 1L;
        version.minor = 0L;
        version.patch = 0L;
        version.status = status;
        version.config = new VersionConfig();
        version.config.version = VersionConfigVersion.V1;
        version.persist();

        return version;
    }
}