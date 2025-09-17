package com.omgservers.omgservice.version;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class VersionResourceClient {

    public Version createCheck201(final Long projectId,
                                  final NewVersion newVersion,
                                  final String token) {
        return create(projectId, newVersion, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(Version.class);
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

    public void getByIdCheck403(final Long versionId, final String token) {
        getById(versionId, token)
                .statusCode(403);
    }

    public Version getByIdCheck200(final Long versionId, final String token) {
        return getById(versionId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Version.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long versionId,
                                         final int statusCode,
                                         final String token) {
        return getById(versionId, token)
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
                .post("/project/{projectId}/version")
                .then()
                .log().all();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long versionId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("id", versionId)
                .log().all()
                .when()
                .get("/version/{id}")
                .then()
                .log().all();
    }
}
