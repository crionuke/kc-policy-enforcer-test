package com.omgservers.omgservice.project;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class ProjectResourceClient {

    public ProjectProjection createCheck201(final Long tenantId,
                                  final NewProject newProject,
                                  final String token) {
        return create(tenantId, newProject, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(ProjectProjection.class);
    }

    public ErrorResponse createCheck4xx(final Long tenantId,
                                        final NewProject newProject,
                                        final int statusCode,
                                        final String token) {
        return create(tenantId, newProject, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public ProjectProjection getByIdCheck200(final Long projectId, final String token) {
        return getById(projectId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ProjectProjection.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long projectId,
                                         final int statusCode,
                                         final String token) {
        return getById(projectId, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    private ValidatableResponseOptions<?, ?> create(final Long tenantId,
                                                    final NewProject newProject,
                                                    final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("tenantId", tenantId)
                .contentType(ContentType.JSON)
                .body(newProject)
                .log().all()
                .when()
                .post("/tenant/{tenantId}/project")
                .then();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long projectId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("id", projectId)
                .log().all()
                .when()
                .get("/project/{id}")
                .then();
    }
}
