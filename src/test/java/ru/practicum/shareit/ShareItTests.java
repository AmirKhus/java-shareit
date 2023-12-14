package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ex.CheckUserNotOwnerItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ShareItTests {

	@Autowired
	private UserService userService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ItemRequestService itemRequestService;

	@Test
	void shareItTest() throws InterruptedException {
		UserDto userDto = UserDto.builder()
				.name("Dmitrij")
				.email("user@user.ru")
				.build();

		UserDto createUser = userService.createUser(userDto);

		assertEquals("userId", 1L, createUser.getId());

		ItemDto itemDto = ItemDto.builder()
				.name("Ролики")
				.description("Ролики 45 размера")
				.available(true)
				.build();

		ItemDto createItem = itemService.createItem(itemDto, createUser.getId());

		assertEquals("itemId", 1L, createItem.getId());

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusSeconds(2);
		BookingDto bookingDto = BookingDto.builder()
				.itemId(createItem.getId())
				.start(start)
				.end(end)
				.build();

		assertThrows(CheckUserNotOwnerItemException.class,
				() -> bookingService.createBooking(bookingDto, createUser.getId()));

		UserDto userDto2 = UserDto.builder()
				.name("NeDmitrij")
				.email("user2@user.ru")
				.build();

		UserDto createUser2 = userService.createUser(userDto2);

		assertEquals("userId", 2L, createUser2.getId());

		BookingDto createbookingDto = bookingService.createBooking(bookingDto, createUser2.getId());

		assertEquals("bookingDto", 1L, createbookingDto.getId());

		bookingService.addStatusBooking(createUser.getId(),
				createbookingDto.getId(), true);

		CommentDto commentDto = CommentDto.builder()
				.text("Отличная вещь")
				.build();

		Thread.sleep(2000);
		CommentDto createComment = itemService.addComment(commentDto,
				createItem.getId(), createUser2.getId());

		assertEquals("commentId", 1L, createComment.getId());

		ItemRequestDto itemRequest = ItemRequestDto.builder()
				.description("Нужны ножницы")
				.build();

		ItemRequestDto createItemRequest = itemRequestService.addRequest(itemRequest,
				createUser.getId());

		assertEquals("itemRequestId", 1L, createItemRequest.getId());

		ItemDto itemDto2 = ItemDto.builder()
				.name("Lampa")
				.description("Lampa of table")
				.available(true)
				.build();

		ItemDto createItem2 = itemService.createItem(itemDto2, createUser2.getId());

		assertEquals("itemId", 2L, createItem2.getId());
		assertEquals("itemAvailable", true, createItem2.getAvailable());

		List<BookingDto> bookingDtoList = bookingService.findAllBookingByUserId(createUser2.getId(), "ALL",
				0, 1);

		assertEquals("bookingDtoListSize", 1, bookingDtoList.size());
	}
}