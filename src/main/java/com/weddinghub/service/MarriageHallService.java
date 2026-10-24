
package com.weddinghub.service;

import com.weddinghub.repository.MarriageHallRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Collectors;
import com.weddinghub.dto.ApiDtos.HallResponse;

@Service
public class MarriageHallService {

    private final MarriageHallRepository marriageHallRepository;

    public MarriageHallService(
            MarriageHallRepository marriageHallRepository) {
        this.marriageHallRepository = marriageHallRepository;
    }

    public List<HallResponse> getAllHalls() {
        return searchHalls(null, null, null);
    }

    public List<HallResponse> searchHalls(String city, Integer minimumCapacity, BigDecimal maximumPrice) {
        if (minimumCapacity != null && minimumCapacity < 1) {
            throw new IllegalArgumentException("Minimum capacity must be greater than zero.");
        }
        if (maximumPrice != null && maximumPrice.signum() <= 0) {
            throw new IllegalArgumentException("Maximum price must be greater than zero.");
        }
        String normalizedCity = city == null || city.isBlank()
                ? null
                : city.trim().toLowerCase(Locale.ROOT);
        return marriageHallRepository.searchActiveHalls(normalizedCity, minimumCapacity, maximumPrice)
                .stream().map(DtoMapper::hall).collect(Collectors.toList());
    }

    public HallResponse getHall(Long id) {
        return marriageHallRepository.findPublicHall(id)
                .map(DtoMapper::hall)
                .orElseThrow(() -> new java.util.NoSuchElementException("Hall not found."));
    }
}
