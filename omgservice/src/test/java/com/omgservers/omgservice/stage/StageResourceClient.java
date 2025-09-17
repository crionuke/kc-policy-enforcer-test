package com.omgservers.omgservice.stage;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class StageResourceClient {

    public Stage createCheck201(final Long tenantId,
                                final NewStage newStage,
                                final String token) {
        return create(tenantId, newStage, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(Stage.class);
    }

    public ErrorResponse createCheck4xx(final Long tenantId,
                                        final NewStage newStage,
                                        final int statusCode,
                                        final String token) {
        return create(tenantId, newStage, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public Stage getByIdCheck200(final Long stageId, final String token) {
        return getById(stageId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Stage.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long stageId,
                                         final int statusCode,
                                         final String token) {
        return getById(stageId, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    private ValidatableResponseOptions<?, ?> create(final Long tenantId,
                                                    final NewStage newStage,
                                                    final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("tenantId", tenantId)
                .contentType(ContentType.JSON)
                .body(newStage)
                .log().all()
                .when()
                .post("/tenant/{tenantId}/stage")
                .then();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long stageId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("id", stageId)
                .log().all()
                .when()
                .get("/stage/{id}")
                .then();
    }
}
