package dk.fakeinfo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.fakeinfo.model.Address;
import dk.fakeinfo.model.PersonName;
import dk.fakeinfo.model.PersonNamesPayload;
import dk.fakeinfo.model.PersonResponse;
import dk.fakeinfo.model.Town;
import dk.fakeinfo.repository.TownRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class FakeInfoService {

    private static final String FEMALE = "female";
    private static final List<String> PHONE_PREFIXES = List.of(
        "2", "30", "31", "40", "41", "42", "50", "51", "52", "53", "60", "61", "71", "81", "91", "92", "93", "342",
        "344", "345", "346", "347", "348", "349", "356", "357", "359", "362", "365", "366", "389", "398", "431",
        "441", "462", "466", "468", "472", "474", "476", "478", "485", "486", "488", "489", "493", "494", "495",
        "496", "498", "499", "542", "543", "545", "551", "552", "556", "571", "572", "573", "574", "577", "579",
        "584", "586", "587", "589", "597", "598", "627", "629", "641", "649", "658", "662", "663", "664", "665",
        "667", "692", "693", "694", "697", "771", "772", "782", "783", "785", "786", "788", "789", "826", "827", "829"
    );
    private static final List<String> LOWERCASE_LETTERS = List.of(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
        "r", "s", "t", "u", "v", "w", "x", "y", "z", "ø", "æ", "å"
    );
    private static final List<String> BASE_CHARACTERS = List.of(
        " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
        "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
        "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
        "Y", "Z"
    );
    private static final List<String> DANISH_CHARACTERS = List.of("æ", "ø", "å", "Æ", "Ø", "Å");
    private static final DateTimeFormatter BIRTH_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final TownRepository townRepository;
    private final ObjectMapper objectMapper;
    private List<PersonName> persons;
    private int townCount;

    public FakeInfoService(TownRepository townRepository, ObjectMapper objectMapper) {
        this.townRepository = townRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void initialize() {
        this.persons = loadPersons();
        this.townCount = townRepository.countTowns();
        if (townCount == 0) {
            throw new IllegalStateException("postal_code table does not contain any rows");
        }
    }

    public FakePerson generateFakePerson() {
        PersonName personName = randomPersonName();
        LocalDate birthDate = randomBirthDate();
        Address address = randomAddress();
        return new FakePerson(
            generateCpr(birthDate, personName.gender()),
            personName.firstName(),
            personName.lastName(),
            personName.gender(),
            birthDate.format(BIRTH_DATE_FORMAT),
            address,
            generatePhoneNumber()
        );
    }

    public List<FakePerson> generateFakePersons(int amount) {
        List<FakePerson> fakePersons = new ArrayList<>(amount);
        for (int index = 0; index < amount; index++) {
            fakePersons.add(generateFakePerson());
        }
        return fakePersons;
    }

    private List<PersonName> loadPersons() {
        try (InputStream inputStream = new ClassPathResource("person-names.json").getInputStream()) {
            PersonNamesPayload payload = objectMapper.readValue(inputStream, PersonNamesPayload.class);
            if (payload.persons() == null || payload.persons().isEmpty()) {
                throw new IllegalStateException("person-names.json does not contain any persons");
            }
            return payload.persons();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load person names", exception);
        }
    }

    private PersonName randomPersonName() {
        return persons.get(randomInt(0, persons.size() - 1));
    }

    private LocalDate randomBirthDate() {
        int year = randomInt(1900, LocalDate.now().getYear());
        int month = randomInt(1, 12);
        int maxDay = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            default -> 28;
        };
        int day = randomInt(1, maxDay);
        return LocalDate.of(year, month, day);
    }

    private String generateCpr(LocalDate birthDate, String gender) {
        int finalDigit = FEMALE.equals(gender) ? randomInt(0, 4) * 2 : randomInt(0, 4) * 2 + 1;
        return "%02d%02d%02d%s%s%s%d".formatted(
            birthDate.getDayOfMonth(),
            birthDate.getMonthValue(),
            birthDate.getYear() % 100,
            randomDigit(),
            randomDigit(),
            randomDigit(),
            finalDigit
        );
    }

    private Address randomAddress() {
        Town town = townRepository.findTownByOffset(randomInt(0, townCount - 1));
        return new Address(
            randomText(40, true),
            randomHouseNumber(),
            randomFloor(),
            randomDoor(),
            town.postalCode(),
            town.townName()
        );
    }

    private String randomHouseNumber() {
        String number = Integer.toString(randomInt(1, 999));
        if (randomInt(1, 10) < 3) {
            number += randomText(1, false).toUpperCase();
        }
        return number;
    }

    private Object randomFloor() {
        if (randomInt(1, 10) < 4) {
            return "st";
        }
        return randomInt(1, 99);
    }

    private Object randomDoor() {
        int doorType = randomInt(1, 20);
        if (doorType < 8) {
            return "th";
        }
        if (doorType < 15) {
            return "tv";
        }
        if (doorType < 17) {
            return "mf";
        }
        if (doorType < 19) {
            return randomInt(1, 50);
        }

        StringBuilder door = new StringBuilder(LOWERCASE_LETTERS.get(randomInt(0, LOWERCASE_LETTERS.size() - 1)));
        if (doorType == 20) {
            door.append("-");
        }
        door.append(randomInt(1, 999));
        return door.toString();
    }

    private String randomText(int length, boolean includeDanishCharacters) {
        List<String> characters = new ArrayList<>(BASE_CHARACTERS);
        if (includeDanishCharacters) {
            characters.addAll(DANISH_CHARACTERS);
        }

        StringBuilder text = new StringBuilder(characters.get(randomInt(1, characters.size() - 1)));
        for (int index = 1; index < length; index++) {
            text.append(characters.get(randomInt(0, characters.size() - 1)));
        }
        return text.toString();
    }

    private String generatePhoneNumber() {
        StringBuilder phone = new StringBuilder(PHONE_PREFIXES.get(randomInt(0, PHONE_PREFIXES.size() - 1)));
        while (phone.length() < 8) {
            phone.append(randomDigit());
        }
        return phone.toString();
    }

    private String randomDigit() {
        return Integer.toString(randomInt(0, 9));
    }

    private int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public record FakePerson(
        String cpr,
        String firstName,
        String lastName,
        String gender,
        String birthDate,
        Address address,
        String phoneNumber
    ) {
        public PersonResponse toNameGender() {
            return new PersonResponse(null, firstName, lastName, gender, null, null, null);
        }

        public PersonResponse toNameGenderDob() {
            return new PersonResponse(null, firstName, lastName, gender, birthDate, null, null);
        }

        public PersonResponse toCprNameGender() {
            return new PersonResponse(cpr, firstName, lastName, gender, null, null, null);
        }

        public PersonResponse toCprNameGenderDob() {
            return new PersonResponse(cpr, firstName, lastName, gender, birthDate, null, null);
        }

        public PersonResponse toFullPerson() {
            return new PersonResponse(cpr, firstName, lastName, gender, birthDate, address, phoneNumber);
        }
    }
}
