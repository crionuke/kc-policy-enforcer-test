package com.omgservers.omgservice.tenant;

import com.omgservers.omgservice.errors.ErrorResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class TenantResourceClient {

    public TenantProjection createCheck201(final NewTenant newTenant, final String token) {
        return create(newTenant, token)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().as(TenantProjection.class);
    }

    public ErrorResponse createCheck4xx(final NewTenant newTenant,
                                        final int statusCode,
                                        final String token) {
        return create(newTenant, token)
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    public TenantProjection getByIdCheck200(final Long tenantId, final String token) {
        return getById(tenantId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(TenantProjection.class);
    }

    public ErrorResponse getByIdCheck4xx(final Long tenantId,
                                         final int statusCode,
                                         final String token) {
        return getById(tenantId, token)
                .contentType(ContentType.JSON)
                .extract().as(ErrorResponse.class);
    }

    private ValidatableResponseOptions<?, ?> create(final NewTenant newTenant,
                                                    final String token) {
        return given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(newTenant)
                .log().all()
                .when()
                .post("/my/tenants")
                .then()
                .log().all();
    }

    private ValidatableResponseOptions<?, ?> getById(final Long tenantId,
                                                     final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("id", tenantId)
                .log().all()
                .when()
                .get("/tenant/{id}")
                .then()
                .log().all();
    }
}
