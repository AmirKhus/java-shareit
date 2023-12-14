package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemUserIdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findAllByItemUserIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, PageRequest pageRequest);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, PageRequest pageRequest);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(Long userId, Status status, PageRequest pageRequest);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long userId, Status status, LocalDateTime dateTime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long id, Long id1, LocalDateTime now);
}
