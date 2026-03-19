package dk.fakeinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.fakeinfo.model.Address;
import dk.fakeinfo.model.PersonResponse;
import dk.fakeinfo.model.Town;
import dk.fakeinfo.repository.TownRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FakeInfoServiceTest {

    @Mock
    private TownRepository townRepository;

    private FakeInfoService fakeInfoService;

    @BeforeEach
    void setUp() {
        fakeInfoService = new FakeInfoService(townRepository, new ObjectMapper());
        when(townRepository.countTowns()).thenReturn(1);
        when(townRepository.findTownByOffset(0)).thenReturn(new Town("8000", "Aarhus C"));
        fakeInfoService.initialize();
    }


    // ------ Test eksempler for unit test med JUnit og Mockito ------
//    @Test
//    void generateFakePersonReturnsCompletePayload() {
//        FakeInfoService.FakePerson fakePerson = fakeInfoService.generateFakePerson();
//
//        assertEquals(10, fakePerson.cpr().length());
//        assertTrue(fakePerson.cpr().chars().allMatch(Character::isDigit));
//        assertFalse(fakePerson.firstName().isBlank());
//        assertFalse(fakePerson.lastName().isBlank());
//        assertTrue(fakePerson.gender().equals("female") || fakePerson.gender().equals("male"));
//        assertTrue(fakePerson.birthDate().matches("\\d{4}-\\d{2}-\\d{2}"));
//        assertEquals(8, fakePerson.phoneNumber().length());
//        assertTrue(fakePerson.phoneNumber().chars().allMatch(Character::isDigit));
//
//        Address address = fakePerson.address();
//        assertNotNull(address);
//        assertEquals("8000", address.postalCode());
//        assertEquals("Aarhus C", address.townName());
//        assertFalse(address.street().isBlank());
//        assertFalse(address.number().isBlank());
//    }
//
//    @Test
//    void generateFakePersonsReturnsRequestedAmount() {
//        List<FakeInfoService.FakePerson> fakePersons = fakeInfoService.generateFakePersons(3);
//
//        assertEquals(3, fakePersons.size());
//    }
//
//    @Test
//    void fullPersonViewContainsAddressAndPhone() {
//        PersonResponse response = fakeInfoService.generateFakePerson().toFullPerson();
//
//        assertEquals(10, response.CPR().length());
//        assertNotNull(response.address());
//        assertEquals(8, response.phoneNumber().length());
//    }
}
