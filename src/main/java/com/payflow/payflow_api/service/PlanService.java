package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.PlanDTO;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository)
    {
        this.planRepository=planRepository;
    }

    public PlanDTO createPlan(PlanDTO dto)
    {
        Plan plan=new Plan();
        plan.setPlanName(dto.planName());
        plan.setBillingCycle(dto.billingCycle());
        plan.setPrice(dto.price());

        planRepository.save(plan);

        return convertToDTO(plan);
    }

    public List<PlanDTO> getPlans()
    {
        return planRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PlanDTO getPlanById(Long id)
    {
        Plan plan=planRepository.findById(id).orElseThrow(()->new RuntimeException("Plan not found !"));
        return convertToDTO(plan);
    }

    public PlanDTO updatePlan(Long id,PlanDTO dto)
    {
        Plan plan =planRepository.findById(id).orElseThrow(()->new RuntimeException("Plan not found !"));

        plan.setPrice(dto.price());
        plan.setBillingCycle(dto.billingCycle());
        plan.setPlanName(dto.planName());

        Plan updatedPlan=planRepository.save(plan);

        return convertToDTO(updatedPlan);
    }

    public void deletePlan(Long id)
    {
        if(!planRepository.existsById(id))
        {
            throw new RuntimeException("Plan not found !");
        }
        planRepository.deleteById(id);
    }

    public PlanDTO convertToDTO(Plan plan)
    {
        return new PlanDTO(plan.getId(), plan.getPlanName(),plan.getPrice(),plan.getBillingCycle());
    }


}
