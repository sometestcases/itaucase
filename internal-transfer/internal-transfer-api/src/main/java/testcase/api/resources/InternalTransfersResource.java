package testcase.api.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import testcase.api.requests.InternalTransferRequest;
import testcase.domain.integrations.balanceManager.exception.BalanceManagerOperationException;
import testcase.domain.service.InternalTransferService;
import testcase.domain.service.exception.InternalTransferServerException;

@RequestMapping("/internal-transfers")
@RestController
public class InternalTransfersResource {

    @Autowired
    private InternalTransferService internalTransferService;

    @PostMapping(path = "/{accountIdFrom}/to/{accountIdTo}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> executeInternalTransfer(
            @PathVariable("accountIdFrom") String accountIdFrom,
            @PathVariable("accountIdTo") String accountIdTo,
            @Validated @RequestBody InternalTransferRequest internalTransferRequest) throws
            InternalTransferServerException, BalanceManagerOperationException {

        this.internalTransferService.internalTransfer(
                internalTransferRequest.getOperationId(),
                accountIdTo,
                accountIdFrom,
                internalTransferRequest.getValue());

        return ResponseEntity.ok().build();
    }
}
