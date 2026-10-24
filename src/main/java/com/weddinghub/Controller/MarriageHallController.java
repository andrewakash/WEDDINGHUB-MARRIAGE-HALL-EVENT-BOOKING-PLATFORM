package com.weddinghub.Controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weddinghub.dto.ApiDtos.HallResponse;
import com.weddinghub.service.MarriageHallService;

@Validated
@RestController
@RequestMapping("/api/v1/halls")
public class MarriageHallController {
    private final MarriageHallService marriageHallService;

    public MarriageHallController(MarriageHallService marriageHallService) {
        this.marriageHallService = marriageHallService;
    }

    @GetMapping
    public List<HallResponse> getAllHalls() {
        return marriageHallService.getAllHalls();
    }

    @GetMapping("/search")
    public List<HallResponse> search(
            @RequestParam(required = false) @Size(max = 100, message = "City must be 100 characters or fewer.") String city,
            @RequestParam(required = false) @Min(value = 1, message = "Minimum capacity must be greater than zero.") Integer minimumCapacity,
            @RequestParam(required = false) @DecimalMin(value = "0", inclusive = false, message = "Maximum price must be greater than zero.") BigDecimal maximumPrice) {
        return marriageHallService.searchHalls(city, minimumCapacity, maximumPrice);
    }

    @GetMapping("/{id}")
    public HallResponse getHall(@PathVariable @Positive(message = "Hall ID must be greater than zero.") Long id) {
        return marriageHallService.getHall(id);
    }
}
