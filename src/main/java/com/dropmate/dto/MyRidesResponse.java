package com.dropmate.dto;

import java.util.List;

import com.dropmate.entity.Rides;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyRidesResponse {
    private List<Rides> created;
    private List<Rides> booked;
}