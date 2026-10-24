package com.weddinghub.Controller;

import java.util.List;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.CateringResponse;
import com.weddinghub.dto.ApiDtos.DecorationResponse;
import com.weddinghub.service.VendorManagementService;

@Validated
@RestController
@RequestMapping("/api/v1")
public class PublicPackageController {
    private final VendorManagementService vendorService;

    public PublicPackageController(VendorManagementService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/catering-packages")
    public List<CateringResponse> catering() {
        return vendorService.publicCatering();
    }

    @GetMapping("/catering-packages/{id}")
    public CateringResponse catering(@PathVariable @Positive Long id) {
        return vendorService.publicCatering(id);
    }

    @GetMapping("/decoration-packages")
    public List<DecorationResponse> decorations() {
        return vendorService.publicDecorations();
    }

    @GetMapping("/decoration-packages/{id}")
    public DecorationResponse decoration(@PathVariable @Positive Long id) {
        return vendorService.publicDecoration(id);
    }
}
