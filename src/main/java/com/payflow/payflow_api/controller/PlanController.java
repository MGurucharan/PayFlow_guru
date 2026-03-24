package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.PlanDTO;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService)
    {
        this.planService=planService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanDTO createPlan(@Valid @RequestBody PlanDTO dto)
    {
        return planService.createPlan(dto);
    }

    @GetMapping
    public List<PlanDTO> getInvoices()
    {
        return planService.getPlans();
    }

    @PutMapping("/{id}")
    public PlanDTO updateInvoice(@Valid @PathVariable Long id,@RequestBody PlanDTO dto)
    {
        return planService.updatePlan(id,dto);
    }

    @GetMapping("/{id}")
    public PlanDTO getInvoiceById(@Valid @PathVariable Long id)
    {
        return planService.getPlanById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@Valid @PathVariable Long id)
    {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();

    }
}
