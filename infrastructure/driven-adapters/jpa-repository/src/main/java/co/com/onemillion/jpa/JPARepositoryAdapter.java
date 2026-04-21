package co.com.onemillion.jpa;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements LeadRepository {
    private final JPARepository repository;

    @Override
    public Lead save(Lead lead) {
        try {
            return toDomain(repository.save(toData(lead)));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateLeadException(lead.getEmail());
        }
    }

    @Override
    public Optional<Lead> findById(Long id) {
        return repository.findById(id)
                .filter(data -> !data.isDeleted())
                .map(this::toDomain);
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        return repository.findByEmailIgnoreCaseAndDeletedFalse(email).map(this::toDomain);
    }

    @Override
    public LeadPage findAll(LeadFilter filter) {
        int safePage = Math.max(filter.getPage(), 0);
        int safeLimit = Math.max(filter.getLimit(), 1);
        PageRequest pageRequest = PageRequest.of(safePage, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LeadData> page = repository.findAll(
                filterBy(filter.getSource(), filter.getStartDate(), filter.getEndDate()),
                pageRequest
        );

        return LeadPage.builder()
                .data(page.stream().map(this::toDomain).toList())
                .page(filter.getPage())
                .limit(filter.getLimit())
                .total(page.getTotalElements())
                .build();
    }

    @Override
    public Lead update(Lead lead) {
        return save(lead);
    }

    @Override
    public boolean softDelete(Long id) {
        return repository.findById(id)
                .filter(data -> !data.isDeleted())
                .map(data -> {
                    data.setDeleted(true);
                    data.setUpdatedAt(LocalDateTime.now());
                    repository.save(data);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCaseAndDeletedFalse(email);
    }

    @Override
    public long countAll() {
        return repository.countByDeletedFalse();
    }

    @Override
    public Map<LeadSource, Long> countByFuente() {
        Map<LeadSource, Long> counts = new EnumMap<>(LeadSource.class);
        repository.countByFuente().forEach(row -> counts.put((LeadSource) row[0], (Long) row[1]));
        return counts;
    }

    @Override
    public BigDecimal averagePresupuesto() {
        BigDecimal average = repository.averagePresupuesto();
        return average == null ? BigDecimal.ZERO : average;
    }

    @Override
    public long countLast7Days() {
        return repository.countByCreatedAtAfterAndDeletedFalse(LocalDateTime.now().minusDays(7));
    }

    private Specification<LeadData> filterBy(LeadSource fuente, LocalDateTime createdAtFrom,
                                             LocalDateTime createdAtTo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            if (fuente != null) {
                predicates.add(criteriaBuilder.equal(root.get("fuente"), fuente));
            }
            if (createdAtFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));
            }
            if (createdAtTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private LeadData toData(Lead lead) {
        return LeadData.builder()
                .id(lead.getId())
                .nombre(lead.getNombre())
                .email(lead.getEmail())
                .telefono(lead.getTelefono())
                .fuente(lead.getFuente())
                .productoInteres(lead.getProductoInteres())
                .presupuesto(lead.getPresupuesto())
                .deleted(lead.isDeleted())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }

    private Lead toDomain(LeadData data) {
        return Lead.builder()
                .id(data.getId())
                .nombre(data.getNombre())
                .email(data.getEmail())
                .telefono(data.getTelefono())
                .fuente(data.getFuente())
                .productoInteres(data.getProductoInteres())
                .presupuesto(data.getPresupuesto())
                .deleted(data.isDeleted())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }
}
