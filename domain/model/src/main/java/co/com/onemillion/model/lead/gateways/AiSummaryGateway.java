package co.com.onemillion.model.lead.gateways;

import co.com.onemillion.model.lead.Lead;

import java.util.List;

/**
 * Puerto de salida para la generación de resúmenes ejecutivos utilizando IA.
 */
public interface AiSummaryGateway {

    /**
     * Genera un resumen ejecutivo basado en una lista de leads.
     *
     * @param leads Lista de leads a procesar.
     * @return Texto con el resumen generado por la IA.
     */
    String generateLeadSummary(List<Lead> leads);
}
