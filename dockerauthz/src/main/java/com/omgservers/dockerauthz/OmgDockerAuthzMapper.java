package com.omgservers.dockerauthz;

import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.docker.DockerAuthV2Protocol;
import org.keycloak.protocol.docker.mapper.DockerAuthV2AttributeMapper;
import org.keycloak.protocol.docker.mapper.DockerAuthV2ProtocolMapper;
import org.keycloak.representations.docker.DockerAccess;
import org.keycloak.representations.docker.DockerResponseToken;

public class OmgDockerAuthzMapper extends DockerAuthV2ProtocolMapper implements DockerAuthV2AttributeMapper {

    @Override
    public String getDisplayType() {
        return "OMGSERVERS Docker v2 protocol mapper";
    }

    @Override
    public String getHelpText() {
        return "Allows all grants, returning the full set of requested access attributes as permitted attributes.";
    }

    @Override
    public String getId() {
        return "omg-docker-authz-mapper";
    }

    @Override
    public boolean appliesTo(final DockerResponseToken responseToken) {
        return true;
    }

    @Override
    public DockerResponseToken transformDockerResponseToken(final DockerResponseToken responseToken,
                                                            final ProtocolMapperModel mappingModel,
                                                            final KeycloakSession session,
                                                            final UserSessionModel userSession,
                                                            final AuthenticatedClientSessionModel clientSession) {

        responseToken.getAccessItems().clear();

        final String requestedScopes = clientSession.getNote(DockerAuthV2Protocol.SCOPE_PARAM);
        if (requestedScopes != null) {
            for (String requestedScope : requestedScopes.split(" ")) {
                final DockerAccess requestedAccess = new DockerAccess(requestedScope);
                responseToken.getAccessItems().add(requestedAccess);
            }
        }

        return responseToken;
    }
}
