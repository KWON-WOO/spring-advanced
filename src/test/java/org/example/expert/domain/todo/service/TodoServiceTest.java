package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.config.CustomException;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private TodoService todoService;
    AuthUser authUser;
    private Todo todo;
    @Mock
    WeatherClient weather;
    @BeforeEach
    void setup() {
        authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        todo = new Todo("title", "content", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());
    }

    @Test
    void saveTodoTest() {
        TodoSaveRequest saveRequest = new TodoSaveRequest(todo.getTitle(), todo.getContents());
        when(weather.getTodayWeather()).thenReturn("weather");
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoSaveResponse savedTodo = todoService.saveTodo(authUser, saveRequest);

        assertThat(savedTodo.getId()).isEqualTo(todo.getId());
        assertThat(savedTodo.getTitle()).isEqualTo(todo.getTitle());
        assertThat(savedTodo.getWeather()).isEqualTo(todo.getWeather());
        assertThat(savedTodo.getContents()).isEqualTo(todo.getContents());
        assertThat(savedTodo.getUser().getId()).isEqualTo(todo.getUser().getId());
    }

    @Test
    void getTodosTest() {
        Page<Todo> mockPage = new PageImpl<>(List.of(todo));
        when(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).thenReturn(mockPage);

        Page<TodoResponse> todoResponses = todoService.getTodos(1,10);

        assertEquals(1, todoResponses.getTotalElements());
        TodoResponse response = todoResponses.getContent().get(0);
        assertThat(response.getId()).isEqualTo(todo.getId());
        assertThat(response.getTitle()).isEqualTo(todo.getTitle());
        assertThat(response.getContents()).isEqualTo(todo.getContents());
        assertThat(response.getWeather()).isEqualTo(todo.getWeather());
        assertThat(response.getUser().getId()).isEqualTo(response.getUser().getId());
    }

    @Test
    void getTodoTest() {
        when(todoRepository.findByIdWithUser(any(Long.class))).thenReturn(Optional.of(todo));
        
        TodoResponse response = todoService.getTodo(1L);

        assertThat(response.getId()).isEqualTo(todo.getId());
        assertThat(response.getTitle()).isEqualTo(todo.getTitle());
        assertThat(response.getContents()).isEqualTo(todo.getContents());
        assertThat(response.getWeather()).isEqualTo(todo.getWeather());
        assertThat(response.getUser().getId()).isEqualTo(todo.getUser().getId());
    }

    @Test
    void getTodo_없는_일정_조회_시_에러를_던진다(){
        Long todoId = 2L;
        when(todoRepository.findByIdWithUser(todoId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> todoService.getTodo(todoId));
        assertEquals("Todo not found", exception.getMessage());
    }
}