package co.com.onemillion.api;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.api.mapper.LeadRestMapper;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.usecase.createlead.CreateLeadUseCase;
import co.com.onemillion.usecase.getleadbyid.GetLeadByIdUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/leads", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
    private final CreateLeadUseCase createLeadUseCase;
    private final GetLeadByIdUseCase getLeadByIdUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody CreateLeadRequest request) {
        Lead createdLead = createLeadUseCase.execute(LeadRestMapper.toDomain(request));
        return ResponseEntity
                .created(URI.create("/leads/" + createdLead.getId()))
                .body(LeadRestMapper.toResponse(createdLead));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable("id") Long id) {
        Lead lead = getLeadByIdUseCase.execute(id);
        return ResponseEntity.ok(LeadRestMapper.toResponse(lead));
    }
}
