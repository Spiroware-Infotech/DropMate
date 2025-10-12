package com.dropmate.service;

import com.dropmate.dto.DeliveryRequest;
import com.dropmate.dto.TripResponse;
import com.dropmate.entity.Trip;
import com.dropmate.enums.TripStatus;
import com.dropmate.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final TripRepository tripRepository;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	/* ------------------ CREATE ------------------ */

	@Transactional
	public TripResponse createDeliveryTrip(DeliveryRequest request) {
		Trip trip = new Trip();

		// trip.setDriverId(request.getDriverId());
		// trip.setVehicleType(request.getVehicleType() != null ?
		// request.getVehicleType() : "truck");
		trip.setOriginName(request.getOriginName());
		trip.setDestinationName(request.getDestinationName());

		if (request.getStartTime() != null) {
			//trip.setStartDate(LocalDateTime.parse(request.getStartTime(), formatter));
		}
		if (request.getEndTime() != null) {
			//trip.setEndTime(LocalDateTime.parse(request.getEndTime(), formatter));
		}

		trip.setSeatsTotal(0);
		trip.setSeatsAvailable(0);
		trip.setCargoSlotsTotal(request.getCargoSlots());
		trip.setCargoSlotsAvailable(request.getCargoSlots());
		trip.setPricePerKg(request.getPricePerKg());
		trip.setStatus(TripStatus.SCHEDULED);

		Trip saved = tripRepository.save(trip);
		return mapToResponse(saved);
	}

	/* ------------------ READ ------------------ */

	public TripResponse getDeliveryById(String tripId) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Delivery trip not found"));
		return mapToResponse(trip);
	}

	public List<TripResponse> getAllDeliveries() {
		return tripRepository.findAll().stream().filter(t -> t.getCargoSlotsTotal() > 0) // only delivery trips
				.map(this::mapToResponse).collect(Collectors.toList());
	}

	public List<TripResponse> getDeliveriesByDriver(Long driverId) {
		return tripRepository.findByUser_UserId(driverId).stream().filter(t -> t.getCargoSlotsTotal() > 0)
				.map(this::mapToResponse).collect(Collectors.toList());
	}

	/* ------------------ UPDATE ------------------ */

	@Transactional
	public TripResponse updateDeliveryTrip(String tripId, DeliveryRequest request) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Delivery trip not found"));

		if (request.getOriginName() != null)
			trip.setOriginName(request.getOriginName());
		if (request.getDestinationName() != null)
			trip.setDestinationName(request.getDestinationName());
		if (request.getStartTime() != null) {
			//trip.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
		}
		if (request.getEndTime() != null) {
			//trip.setEndTime(LocalDateTime.parse(request.getEndTime(), formatter));
		}
		if (request.getCargoSlots() != null) {
			trip.setCargoSlotsTotal(request.getCargoSlots());
			trip.setCargoSlotsAvailable(request.getCargoSlots());
		}
		if (request.getPricePerKg() != null)
			trip.setPricePerKg(request.getPricePerKg());

		Trip updated = tripRepository.save(trip);
		return mapToResponse(updated);
	}

	/* ------------------ DELETE ------------------ */

	@Transactional
	public void deleteDeliveryTrip(String tripId) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Delivery trip not found"));
		tripRepository.delete(trip);
	}

	/* ------------------ HELPER ------------------ */

	private TripResponse mapToResponse(Trip trip) {
		TripResponse resp = new TripResponse();
		resp.setId(trip.getId());
		resp.setDriverId(trip.getUser().getUserId());
		resp.setVehicleType(trip.getVehicleType().name());
		resp.setOriginName(trip.getOriginName());
		resp.setDestinationName(trip.getDestinationName());
		resp.setStartTime(trip.getStartTime() != null ? trip.getStartTime().toString() : null);
		//resp.setEndTime(trip.getEndTime() != null ? trip.getEndTime().toString() : null);
		resp.setSeatsTotal(trip.getSeatsTotal());
		resp.setSeatsAvailable(trip.getSeatsAvailable());
		resp.setCargoSlotsTotal(trip.getCargoSlotsTotal());
		resp.setCargoSlotsAvailable(trip.getCargoSlotsAvailable());
		resp.setPricePerKg(trip.getPricePerKg());
		resp.setStatus(trip.getStatus().name());
		return resp;
	}

	public Object getDeliveryById(Long deliveryId) {
		// TODO Auto-generated method stub
		return null;
	}
}
