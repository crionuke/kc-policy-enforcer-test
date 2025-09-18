package com.omgservers.omgservice.version;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class VersionResourceClient {

    public VersionProjection createCheck201(final Long projectId,
                                  final NewVersion newVersion,
                                  final String token) {
        return create(projectId, newVersion, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(VersionProjection.class);
    }

    public ErrorResponse createCheck4xx(final Long projectId,
                                        final NewVersion newVersion,
                                        final int statusCode,
                                        final String token) {
        return create(projectId, newVersion, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public VersionProjection getByIdCheck200(final Long projectId,
                                   final Long versionId,
                                   final String token) {
        return getById(projectId, versionId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(VersionProjection.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long projectId,
                                         final Long versionId,
                                         final int statusCode,
                                         final String token) {
        return getById(projectId, versionId, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    private ValidatableResponseOptions<?, ?> create(final Long projectId,
                                                    final NewVersion newVersion,
                                                    final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("projectId", projectId)
                .contentType(ContentType.JSON)
                .body(newVersion)
                .log().all()
                .when()
                .post("/projects/{projectId}/versions")
                .then()
                .log().all();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long projectId,
                                                     final Long versionId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("projectId", projectId)
                .pathParam("id", versionId)
                .log().all()
                .when()
                .get("/projects/{projectId}/versions/{id}")
                .then()
                .log().all();
    }
}
