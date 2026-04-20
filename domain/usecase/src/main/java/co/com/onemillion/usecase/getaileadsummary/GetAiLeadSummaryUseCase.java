package co.com.onemillion.usecase.getaileadsummary;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.gateways.AiSummaryGateway;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetAiLeadSummaryUseCase {
    private final LeadRepository leadRepository;
    private final AiSummaryGateway aiSummaryGateway;

    public String execute() {
        // Obtenemos los leads activos (sin filtros para el resumen general)
        // Usamos un límite alto para tener una buena base de datos para la IA
        LeadFilter filter = LeadFilter.builder()
                .page(0)
                .limit(100)
                .build();
                
        LeadPage leadPage = leadRepository.findAll(filter);
        List<Lead> leads = leadPage.getData();
        
        return aiSummaryGateway.generateLeadSummary(leads);
    }
}
