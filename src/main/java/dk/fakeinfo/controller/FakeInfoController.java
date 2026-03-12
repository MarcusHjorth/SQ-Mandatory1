package dk.fakeinfo.controller;

import dk.fakeinfo.model.AddressEnvelope;
import dk.fakeinfo.model.CprResponse;
import dk.fakeinfo.model.ErrorResponse;
import dk.fakeinfo.model.PersonResponse;
import dk.fakeinfo.model.PhoneResponse;
import dk.fakeinfo.service.FakeInfoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeInfoController {

    private final FakeInfoService fakeInfoService;

    public FakeInfoController(FakeInfoService fakeInfoService) {
        this.fakeInfoService = fakeInfoService;
    }

    @GetMapping({"/cpr", "/cpr/"})
    public CprResponse getCpr() {
        return new CprResponse(fakeInfoService.generateFakePerson().cpr());
    }

    @GetMapping({"/name-gender", "/name-gender/"})
    public PersonResponse getNameGender() {
        return fakeInfoService.generateFakePerson().toNameGender();
    }

    @GetMapping({"/name-gender-dob", "/name-gender-dob/"})
    public PersonResponse getNameGenderDob() {
        return fakeInfoService.generateFakePerson().toNameGenderDob();
    }

    @GetMapping({"/cpr-name-gender", "/cpr-name-gender/"})
    public PersonResponse getCprNameGender() {
        return fakeInfoService.generateFakePerson().toCprNameGender();
    }

    @GetMapping({"/cpr-name-gender-dob", "/cpr-name-gender-dob/"})
    public PersonResponse getCprNameGenderDob() {
        return fakeInfoService.generateFakePerson().toCprNameGenderDob();
    }

    @GetMapping({"/address", "/address/"})
    public AddressEnvelope getAddress() {
        return new AddressEnvelope(fakeInfoService.generateFakePerson().address());
    }

    @GetMapping({"/phone", "/phone/"})
    public PhoneResponse getPhone() {
        return new PhoneResponse(fakeInfoService.generateFakePerson().phoneNumber());
    }

    @GetMapping({"/person", "/person/"})
    public ResponseEntity<?> getPerson(@RequestParam(name = "n", defaultValue = "1") int amount) {
        if (amount <= 0 || amount > 100) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Incorrect GET parameter value"));
        }

        if (amount == 1) {
            return ResponseEntity.ok(fakeInfoService.generateFakePerson().toFullPerson());
        }

        List<PersonResponse> persons = fakeInfoService.generateFakePersons(amount).stream()
            .map(person -> person.toFullPerson())
            .toList();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/")
    public ResponseEntity<ErrorResponse> root() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Incorrect API endpoint"));
    }
}
