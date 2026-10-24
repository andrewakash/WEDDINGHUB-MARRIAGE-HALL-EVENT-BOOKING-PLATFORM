package com.weddinghub.service;

import com.weddinghub.dto.ApiDtos.CateringResponse;
import com.weddinghub.dto.ApiDtos.DecorationResponse;
import com.weddinghub.dto.ApiDtos.HallResponse;
import com.weddinghub.entity.CateringPackage;
import com.weddinghub.entity.DecorationPackage;
import com.weddinghub.entity.MarriageHall;

final class DtoMapper {
    private DtoMapper() { }

    static HallResponse hall(MarriageHall hall) {
        return new HallResponse(hall.getId(), hall.getVendor().getId(), hall.getName(), hall.getDescription(),
                hall.getCity(), hall.getAddress(), hall.getCapacity(), hall.getPricePerDay(),
                hall.getStatus(), hall.getCreatedAt());
    }

    static CateringResponse catering(CateringPackage item) {
        return new CateringResponse(item.getId(), item.getVendor().getId(), item.getVendor().getBusinessName(),
                item.getName(), item.getDescription(), item.getFoodType(), item.getPricePerPerson(),
                item.getStatus(), item.getCreatedAt());
    }

    static DecorationResponse decoration(DecorationPackage item) {
        return new DecorationResponse(item.getId(), item.getVendor().getId(), item.getVendor().getBusinessName(),
                item.getName(), item.getDescription(), item.getPrice(), item.getStatus(), item.getCreatedAt());
    }
}
