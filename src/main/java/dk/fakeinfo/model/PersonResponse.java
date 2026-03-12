package dk.fakeinfo.model;

public record PersonResponse(
    String CPR,
    String firstName,
    String lastName,
    String gender,
    String birthDate,
    Address address,
    String phoneNumber
) {
}
