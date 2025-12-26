package com.bookingservice.BookingService.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServerEntity {

    private Long id;
    private String name;
    private String email;
    private String status; // ACTIVE / BLOCKED

    // getters & setters
}
