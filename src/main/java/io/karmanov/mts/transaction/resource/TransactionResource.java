package io.karmanov.mts.transaction.resource;

import io.karmanov.mts.transaction.dto.TransactionRequest;
import io.karmanov.mts.transaction.model.Transaction;
import io.karmanov.mts.transaction.service.TransactionCreationDelegate;
import io.karmanov.mts.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    final TransactionService transactionService;
    final TransactionCreationDelegate transactionCreationDelegate;

    @Inject
    public TransactionResource(TransactionService transactionService, TransactionCreationDelegate transactionCreationDelegate) {
        this.transactionService = transactionService;
        this.transactionCreationDelegate = transactionCreationDelegate;
    }

    @GET
    public Response listAll() {
        List<Transaction> transactions = transactionService.listAll();
        return Response.ok(transactions).build();
    }

    @POST
    public Response create(TransactionRequest transactionRequest,
                           @DefaultValue("0") @QueryParam("delay") Long delay) {
        try {
            Transaction transaction = transactionCreationDelegate.executeTransaction(transactionRequest, delay);
            return Response.status(Response.Status.CREATED).entity(transaction).build();
        } catch (BadRequestException e) {
            log.warn("-- crete()", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("-- crete()", e);
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
