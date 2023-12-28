package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long userId, PageRequest pageRequest);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId,
                                                             LocalDateTime dateTime,
                                                             PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId,
                                                                 LocalDateTime dateTime,
                                                                 PageRequest pageRequest);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId,
                                                          BookingStatus state,
                                                          PageRequest pageRequest);

    @Query("select b from Booking b " +
            "where user_id = ?1 " +
            "and (b.status = ?2 or b.status = ?3 or b.status = ?4)" +
            "and (b.end > localtimestamp and b.start < localtimestamp) " +
            "order by b.start desc")
    List<Booking> getBookingCurrentByUserId(
            long userId, BookingStatus waiting, BookingStatus rejected, BookingStatus current);

    @Query("select b from Booking b " +
           "inner join b.item i " +
           "where i.owner.id = ?1 " +
           "order by b.start desc")
    List<Booking> getAllBookingByOwnerId(long userId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "inner join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.end < localtimestamp " +
            "order by b.start desc")
    List<Booking> getPastBookingByOwnerId(long userId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "inner join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.start > localtimestamp " +
            "order by b.start desc")
    List<Booking> getFutureBookingByOwnerId(long userId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "inner join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> getBookingWithStatusByOwnerId(long userId, BookingStatus state, PageRequest pageRequest);

    @Query("select b from Booking b " +
           "inner join b.item i " +
           "where i.owner.id = ?1 " +
           "and (b.status = ?2 or b.status = ?3 or b.status = ?4)" +
           "and (b.end > localtimestamp and b.start < localtimestamp) " +
           "order by b.start desc")
    List<Booking> getBookingCurrentByOwnerId(
            long userId,
            BookingStatus waiting,
            BookingStatus rejected,
            BookingStatus current,
            PageRequest pageRequest);

    @Query("select b from Booking b " +
           "inner join b.item i " +
           "where i.id = ?1 " +
           "and b.item.id = ?1 " +
           "and b.booker.id = ?2 " +
           "and b.end < localtimestamp")
    Optional<List<Booking>> getBookingItemWhichTookUser(long itemId, long userId);

    List<Booking> findByItemIdAndStatusIsNot(long itemId, BookingStatus status);

}
