package com.omgservers.omgservice.event;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponseOptions;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static io.restassured.RestAssured.given;

@ApplicationScoped
public class EventResourceClient {

    public Event getByQualifierAndResourceIdCheck200(final EventQualifier qualifier,
                                                     final Long resourceId,
                                                     final String token) {
        return getByQualifierAndResourceId(qualifier, resourceId, token)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(Events.class)
                .list.getFirst();
    }

    public void processByIdCheck204(final Long id,
                                    final String token) {
        processById(id, token)
                .statusCode(204);
    }

    private ValidatableResponseOptions<?, ?> getByQualifierAndResourceId(final EventQualifier qualifier,
                                                                         final Long resourceId,
                                                                         final String token) {
        return given()
                .auth().oauth2(token)
                .queryParam("qualifiers", List.of(qualifier))
                .queryParam("resourceIds", List.of(resourceId))
                .log().all()
                .when()
                .get("/platform/events")
                .then();
    }

    private ValidatableResponseOptions<?, ?> processById(final Long id,
                                                         final String token) {
        return given()
                .auth().oauth2(token)
                .pathParam("id", id)
                .log().all()
                .when()
                .post("/platform/events/{id}/process")
                .then()
                .log().all();
    }
}
