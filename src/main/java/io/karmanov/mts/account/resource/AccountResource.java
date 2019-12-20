package io.karmanov.mts.account.resource;

import io.karmanov.mts.account.model.Account;
import io.karmanov.mts.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;

    AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    public Response listAll() {
        List<Account> accounts = accountService.listAll();
        GenericEntity<Collection<Account>> entity = new GenericEntity<>(accounts, List.class);
        return Response.ok(entity).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {
        try {
            Account account = accountService.findById(id);
            return Response.ok(account).build();
        } catch (EntityNotFoundException e) {
            log.error("-- findById()", e);
            return Response.status(Response.Status.NOT_FOUND).entity("Account with id " + id + " not found").build();
        }
    }
}