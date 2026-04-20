package co.com.onemillion.api;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadPageResponse;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.api.dto.LeadStatsResponse;
import co.com.onemillion.api.dto.UpdateLeadRequest;
import co.com.onemillion.api.mapper.LeadRestMapper;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.usecase.createlead.CreateLeadUseCase;
import co.com.onemillion.usecase.deletelead.DeleteLeadUseCase;
import co.com.onemillion.usecase.getleadbyid.GetLeadByIdUseCase;
import co.com.onemillion.usecase.getleadstats.GetLeadStatsUseCase;
import co.com.onemillion.usecase.listleads.ListLeadsUseCase;
import co.com.onemillion.usecase.updatelead.UpdateLeadUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/leads", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
    private final CreateLeadUseCase createLeadUseCase;
    private final GetLeadByIdUseCase getLeadByIdUseCase;
    private final ListLeadsUseCase listLeadsUseCase;
    private final UpdateLeadUseCase updateLeadUseCase;
    private final DeleteLeadUseCase deleteLeadUseCase;
    private final GetLeadStatsUseCase getLeadStatsUseCase;

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

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LeadResponse> updateLead(@PathVariable("id") Long id,
                                                   @Valid @RequestBody UpdateLeadRequest request) {
        LeadPatch patch = LeadRestMapper.toPatch(request);
        Lead updatedLead = updateLeadUseCase.execute(id, patch);
        return ResponseEntity.ok(LeadRestMapper.toResponse(updatedLead));
    }

    @GetMapping
    public ResponseEntity<LeadPageResponse> listLeads(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "source", required = false) String source,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        LeadFilter filter = LeadRestMapper.toFilter(page, limit, source, startDate, endDate);
        LeadPage leads = listLeadsUseCase.execute(filter);
        return ResponseEntity.ok(LeadRestMapper.toPageResponse(leads));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable("id") Long id) {
        deleteLeadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<LeadStatsResponse> getStats() {
        LeadStats stats = getLeadStatsUseCase.execute();
        return ResponseEntity.ok(LeadRestMapper.toStatsResponse(stats));
    }
}
