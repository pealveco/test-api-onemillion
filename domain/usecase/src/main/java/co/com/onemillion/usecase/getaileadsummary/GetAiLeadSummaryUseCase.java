package co.com.onemillion.usecase.getaileadsummary;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.AiSummaryGateway;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetAiLeadSummaryUseCase {
    private final LeadRepository leadRepository;
    private final AiSummaryGateway aiSummaryGateway;

    public String execute(LeadFilter filter) {
        validate(filter);
        
        // Obtenemos los leads filtrados. 
        // Para el resumen usamos un límite alto fijo para capturar suficientes datos.
        LeadFilter domainFilter = filter.toBuilder()
                .page(0)
                .limit(1000) 
                .build();
                
        LeadPage leadPage = leadRepository.findAll(domainFilter);
        List<Lead> leads = leadPage.getData();
        
        return aiSummaryGateway.generateLeadSummary(leads);
    }

    private void validate(LeadFilter filter) {
        if (filter.getStartDate() != null && filter.getEndDate() != null
                && filter.getStartDate().isAfter(filter.getEndDate())) {
            throw new ValidationException("startDate no puede ser mayor que endDate");
        }
    }
}
