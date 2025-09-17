package com.omgservers.omgservice.event;

import com.omgservers.omgservice.base.SortOrder;
import jakarta.inject.Provider;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);

    final EventProcessor eventProcessor;
    final EventService eventService;
    final Provider<String> subClaim;

    public EventResource(final EventProcessor eventProcessor,
                         final EventService eventService,
                         final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.eventProcessor = eventProcessor;
        this.eventService = eventService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/platform/events/{id}")
    public Event getById(@NotNull final Long id) {
        return Event.findByIdRequired(id);
    }

    @POST
    @ResponseStatus(204)
    @Path("/platform/events/{id}/process")
    public void processById(@NotNull final Long id) {
        final var event = Event.findByIdRequired(id);
        LOGGER.info("Processing event {}", id);
        eventProcessor.process(event);
    }

    @GET
    @Path("/platform/events")
    public Events search(@QueryParam("qualifiers") @Size(max = 16) List<EventQualifier> qualifiers,
                         @QueryParam("resourceIds") @Size(max = 32) List<Long> resourceIds,
                         @QueryParam("sort") @Size(max = 3) List<EventSortColumn> sort,
                         @QueryParam("order") @DefaultValue(SortOrder.Constants.ASC) SortOrder order,
                         @QueryParam("page") @DefaultValue("0") @Min(0) int page,
                         @QueryParam("size") @DefaultValue("16") @Max(128) int size) {
        final var list = Event.search(qualifiers, resourceIds, sort, order, page, size);
        final var events = new Events();
        events.size = list.size();
        events.list = list;
        return events;
    }
}
