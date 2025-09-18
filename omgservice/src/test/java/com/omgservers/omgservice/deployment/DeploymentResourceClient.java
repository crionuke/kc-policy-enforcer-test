package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class DeploymentResourceClient {

    public Deployment createCheck201(final Long stageId,
                                     final NewDeployment newDeployment,
                                     final String token) {
        return create(stageId, newDeployment, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(Deployment.class);
    }

    public ErrorResponse createCheck4xx(final Long stageId,
                                        final NewDeployment newDeployment,
                                        final int statusCode,
                                        final String token) {
        return create(stageId, newDeployment, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public Deployment getByIdCheck200(final Long stageId,
                                      final Long deploymentId,
                                      final String token) {
        return getById(stageId, deploymentId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Deployment.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long stageId,
                                         final Long deploymentId,
                                         final int statusCode,
                                         final String token) {
        return getById(stageId, deploymentId, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public Deployments getByProjectIdCheck200(final Long projectId, final String token) {
        return getByProjectId(projectId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Deployments.class);
    }

    private ValidatableResponseOptions<?, ?> create(final Long stageId,
                                                    final NewDeployment newDeployment,
                                                    final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("stageId", stageId)
                .contentType(ContentType.JSON)
                .body(newDeployment)
                .log().all()
                .when()
                .post("/stage/{stageId}/deployment")
                .then()
                .log().all();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long stageId,
                                                     final Long deploymentId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("stageId", stageId)
                .pathParam("id", deploymentId)
                .log().all()
                .when()
                .get("/stage/{stageId}/deployment/{id}")
                .then()
                .log().all();
    }

    private ValidatableResponseOptions<?, ?> getByProjectId(final Long projectId,
                                                            final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("projectId", projectId)
                .log().all()
                .when()
                .get("/project/{projectId}/deployment")
                .then()
                .log().all();
    }
}
