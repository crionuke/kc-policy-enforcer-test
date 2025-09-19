package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.authz.KeycloakService;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegistryAuthzService {

    static private final String TENANT_ID_ATTRIBUTE = "tenant_id";
    static private final String PROJECT_ID_ATTRIBUTE = "project_id";
    static private final String REGISTRY_ID_ATTRIBUTE = "registry_id";
    static private final String REGISTRY_NAME_ATTRIBUTE = "registry_name";

    final KeycloakService keycloakService;

    public RegistryAuthzService(final KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public String getViewersGroupName(final Long registryId) {
        return "group:omg:registry:%d:viewers".formatted(registryId);
    }

    public GroupRepresentation createViewersGroup(final Long tenantId,
                                                  final Long projectId,
                                                  final Long registryId,
                                                  final String registryName) {
        final var name = getViewersGroupName(registryId);
        return keycloakService.createGroup(name, Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                PROJECT_ID_ATTRIBUTE, List.of(projectId.toString()),
                REGISTRY_ID_ATTRIBUTE, List.of(registryId.toString()),
                REGISTRY_NAME_ATTRIBUTE, List.of(registryName)));
    }

    public String getManagersGroupName(final Long registryId) {
        return "group:omg:registry:%d:managers".formatted(registryId);
    }

    public GroupRepresentation createManagersGroup(final Long tenantId,
                                                   final Long projectId,
                                                   final Long registryId,
                                                   final String registryName) {
        final var name = getManagersGroupName(registryId);
        return keycloakService.createGroup(name, Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                PROJECT_ID_ATTRIBUTE, List.of(projectId.toString()),
                REGISTRY_ID_ATTRIBUTE, List.of(registryId.toString()),
                REGISTRY_NAME_ATTRIBUTE, List.of(registryName)));
    }

    public String getAdminsGroupName(final Long registryId) {
        return "group:omg:registry:%d:admins".formatted(registryId);
    }

    public GroupRepresentation createAdminsGroup(final Long tenantId,
                                                 final Long projectId,
                                                 final Long registryId,
                                                 final String registryName) {
        final var name = getAdminsGroupName(registryId);
        return keycloakService.createGroup(name, Map.of(TENANT_ID_ATTRIBUTE, List.of(tenantId.toString()),
                PROJECT_ID_ATTRIBUTE, List.of(projectId.toString()),
                REGISTRY_ID_ATTRIBUTE, List.of(registryId.toString()),
                REGISTRY_NAME_ATTRIBUTE, List.of(registryName)));
    }
}
