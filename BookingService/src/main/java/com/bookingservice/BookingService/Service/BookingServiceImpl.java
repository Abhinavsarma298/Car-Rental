package com.bookingservice.BookingService.Service;

import com.bookingservice.BookingService.Entity.Booking;
import com.bookingservice.BookingService.Entity.RentalState;
import com.bookingservice.BookingService.Repository.BookingRepository;
import com.bookingservice.BookingService.dto.BookingRequest;
import com.bookingservice.BookingService.dto.BookingResponse;
import com.bookingservice.BookingService.exception.AvailabilityException;
import com.bookingservice.BookingService.exception.BookingNotFoundException;
import com.bookingservice.BookingService.exception.InvalidBookingStateException;
import com.bookingservice.BookingService.exception.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    private BookingResponse mapToResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getUserId(),
                b.getVehicleId(),
                b.getPickupDatetime(),
                b.getDropoffDatetime(),
                b.getTotalDistance(),
                b.getStatus(),
                b.getTotalAmount(),
                b.getSecurityDeposit(),
                b.getBookedAt(),
                RentalState.valueOf(b.getStatus().toUpperCase())
        );
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        validateDates(request.getPickupDatetime(), request.getDropoffDatetime());

        boolean available = checkAvailability(request.getVehicleId(), request.getPickupDatetime(), request.getDropoffDatetime());
        if (!available) {
            log.info("Vehicle {} not available for {} - {}", request.getVehicleId(), request.getPickupDatetime(), request.getDropoffDatetime());
            throw new AvailabilityException("Vehicle not available for the requested period");
        }

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setVehicleId(request.getVehicleId());
        booking.setPickupDatetime(request.getPickupDatetime());
        booking.setDropoffDatetime(request.getDropoffDatetime());
        booking.setStatus(RentalState.REQUESTED.name());
        booking.setTotalDistance(request.getEstimatedDistance());

        bookingRepository.save(booking);
        log.debug("Created booking id={} for user={} vehicle={}", booking.getId(), booking.getUserId(), booking.getVehicleId());
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId) {
        Booking b = getBookingOrThrow(bookingId);
        return mapToResponse(b);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserIdOrderByBookedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public BookingResponse modifyBooking(Long bookingId, BookingRequest request) {
        Booking b = getBookingOrThrow(bookingId);

        if (RentalState.valueOf(b.getStatus()).ordinal() >= RentalState.ACTIVE.ordinal()) {
            log.warn("Attempt to modify booking {} in state {}", bookingId, b.getStatus());
            throw new InvalidBookingStateException("Cannot modify booking in current state: " + b.getStatus());
        }

        validateDates(request.getPickupDatetime(), request.getDropoffDatetime());

        boolean available = checkAvailability(request.getVehicleId(), request.getPickupDatetime(), request.getDropoffDatetime());
        // allow keeping same vehicle even if overlap with itself — checkAvailability will include current booking,
        // so we only block if requested vehicle differs and not available.
        if (!available && !b.getVehicleId().equals(request.getVehicleId())) {
            log.info("Vehicle {} not available for new window {} - {}", request.getVehicleId(), request.getPickupDatetime(), request.getDropoffDatetime());
            throw new AvailabilityException("Requested vehicle not available for the new time window");
        }

        b.setPickupDatetime(request.getPickupDatetime());
        b.setDropoffDatetime(request.getDropoffDatetime());
        b.setTotalDistance(request.getEstimatedDistance());
        bookingRepository.save(b);
        log.debug("Modified booking id={}", b.getId());
        return mapToResponse(b);
    }

    @Override
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking b = getBookingOrThrow(bookingId);

        if (!b.getUserId().equals(userId)) {
            log.warn("Unauthorized cancel attempt by user={} on booking={}", userId, bookingId);
            throw new UnauthorizedActionException("Not authorized to cancel this booking");
        }

        if (RentalState.valueOf(b.getStatus()) == RentalState.COMPLETED) {
            log.warn("Attempt to cancel completed booking={}", bookingId);
            throw new InvalidBookingStateException("Cannot cancel a completed booking");
        }

        b.setStatus(RentalState.CANCELLED.name());
        bookingRepository.save(b);
        log.info("Booking {} cancelled by user {}", bookingId, userId);
        return mapToResponse(b);
    }

    @Override
    public BookingResponse confirmBooking(Long bookingId) {
        Booking b = getBookingOrThrow(bookingId);

        if (RentalState.valueOf(b.getStatus()) != RentalState.REQUESTED) {
            log.warn("Attempt to confirm booking {} in state {}", bookingId, b.getStatus());
            throw new InvalidBookingStateException("Only requested bookings can be confirmed");
        }

        // TODO: integrate payment and availability checks here (throw AvailabilityException or custom PaymentException)
        b.setStatus(RentalState.CONFIRMED.name());
        b.setTotalAmount(estimateFare(b.getVehicleId(), b.getPickupDatetime(), b.getDropoffDatetime(), b.getTotalDistance()));
        bookingRepository.save(b);
        log.info("Booking {} confirmed", bookingId);
        return mapToResponse(b);
    }

    @Override
    public BookingResponse startRental(Long bookingId) {
        Booking b = getBookingOrThrow(bookingId);

        if (RentalState.valueOf(b.getStatus()) != RentalState.CONFIRMED) {
            log.warn("Attempt to start booking {} in state {}", bookingId, b.getStatus());
            throw new InvalidBookingStateException("Only confirmed bookings can be started");
        }

        b.setStatus(RentalState.ACTIVE.name());
        bookingRepository.save(b);
        log.info("Booking {} started", bookingId);
        return mapToResponse(b);
    }

    @Override
    public BookingResponse completeRental(Long bookingId) {
        Booking b = getBookingOrThrow(bookingId);

        if (RentalState.valueOf(b.getStatus()) != RentalState.ACTIVE) {
            log.warn("Attempt to complete booking {} in state {}", bookingId, b.getStatus());
            throw new InvalidBookingStateException("Only active bookings can be completed");
        }

        BigDecimal finalFare = estimateFare(b.getVehicleId(), b.getPickupDatetime(), b.getDropoffDatetime(), b.getTotalDistance());
        b.setStatus(RentalState.COMPLETED.name());
        b.setTotalAmount(finalFare);
        bookingRepository.save(b);
        log.info("Booking {} completed, final fare {}", bookingId, finalFare);
        return mapToResponse(b);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long vehicleId, LocalDateTime pickup, LocalDateTime dropoff) {
        if (pickup == null || dropoff == null) {
            log.debug("Availability check called with null pickup/dropoff");
            return false;
        }
        List<Booking> overlapping = bookingRepository.findByVehicleIdAndDropoffDatetimeGreaterThanEqualAndPickupDatetimeLessThanEqual(vehicleId, pickup, dropoff);
        return overlapping.stream()
                .noneMatch(b -> {
                    String s = b.getStatus();
                    return !s.equalsIgnoreCase(RentalState.CANCELLED.name()) &&
                            !s.equalsIgnoreCase(RentalState.COMPLETED.name());
                });
    }

    @Override
    public BigDecimal estimateFare(Long vehicleId, LocalDateTime pickup, LocalDateTime dropoff, BigDecimal distanceKm) {
        validateDates(pickup, dropoff);

        BigDecimal baseRatePerHour = BigDecimal.valueOf(200); // placeholder
        BigDecimal distanceRatePerKm = BigDecimal.valueOf(10); // placeholder

        long minutes = Duration.between(pickup, dropoff).toMinutes();
        if (minutes <= 0) minutes = 1;
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);

        BigDecimal fareByTime = baseRatePerHour.multiply(hours);
        BigDecimal fareByDistance = (distanceKm == null) ? BigDecimal.ZERO : distanceRatePerKm.multiply(distanceKm);

        boolean peak = isPeakHour(pickup);
        BigDecimal subtotal = fareByTime.add(fareByDistance);
        if (peak) {
            subtotal = subtotal.multiply(BigDecimal.valueOf(1.2));
        }

        BigDecimal securityDeposit = subtotal.multiply(BigDecimal.valueOf(0.1));
        return subtotal.add(securityDeposit).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isPeakHour(LocalDateTime dt) {
        int hour = dt.getHour();
        return hour >= 17 && hour <= 20;
    }

    private void validateDates(LocalDateTime pickup, LocalDateTime dropoff) {
        if (pickup == null || dropoff == null) {
            log.warn("Invalid dates: pickup or dropoff is null");
            throw new IllegalArgumentException("Pickup and dropoff must be provided");
        }
        if (dropoff.isBefore(pickup)) {
            log.warn("Invalid dates: dropoff {} is before pickup {}", dropoff, pickup);
            throw new IllegalArgumentException("Dropoff must be after pickup");
        }
        long days = Duration.between(pickup, dropoff).toDays();
        if (days > 30) {
            log.warn("Invalid duration: {} days exceeds limit", days);
            throw new IllegalArgumentException("Maximum rental duration exceeded (30 days)");
        }
    }

    @Override
    public BookingResponse signRentalAgreement(Long bookingId, Long userId, String signatureData) {
        Booking b = getBookingOrThrow(bookingId);

        if (!b.getUserId().equals(userId)) {
            log.warn("Unauthorized signature attempt user={} booking={}", userId, bookingId);
            throw new UnauthorizedActionException("Not authorized to sign this agreement");
        }

        if (b.getStatus().equalsIgnoreCase(RentalState.REQUESTED.name())) {
            b.setStatus(RentalState.CONFIRMED.name());
        }

        // TODO: persist signatureData to storage and link it to booking
        bookingRepository.save(b);
        log.info("Booking {} signed by user {}", bookingId, userId);
        return mapToResponse(b);
    }
}
