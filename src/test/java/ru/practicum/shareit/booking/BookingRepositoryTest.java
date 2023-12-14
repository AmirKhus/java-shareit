package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookingRepositoryTest {

    @Autowired
    public BookingRepository bookingStorage;

    @Autowired
    private TestEntityManager em;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    @Transactional
    public void saveBooking() {
        User owner = User.builder()
                .name("Dmitrij")
                .email("Dimon@mail.ru")
                .build();
        User booker = User.builder()
                .name("NeDmitrij")
                .email("NeDimon@mail.ru")
                .build();
        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(dateTime)
                .end(dateTime.plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertEquals(0, owner.getId());
        em.persist(owner);
        Assertions.assertEquals(1, owner.getId());

        Assertions.assertEquals(0, booker.getId());
        em.persist(booker);
        Assertions.assertEquals(2, booker.getId());

        Assertions.assertEquals(0, item.getId());
        em.persist(item);
        Assertions.assertEquals(1, item.getId());

        Assertions.assertEquals(0, booking.getId());
        em.persist(booking);
        Assertions.assertEquals(1, booking.getId());

        List<Booking> bookings = bookingStorage
                .getBookingCurrentByUserId(2, BookingStatus.APPROVED,
                        BookingStatus.WAITING, BookingStatus.REJECTED);

        Assertions.assertEquals(1, bookings.size());
    }
}
