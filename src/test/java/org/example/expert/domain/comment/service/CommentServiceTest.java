package org.example.expert.domain.comment.service;

import org.aspectj.util.Reflection;
import org.example.expert.config.CustomException;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    private CommentSaveRequest request;
    private AuthUser authUser;
    private User user;
    private Todo todo;
    private Comment comment;

    @BeforeEach
    void setup(){
        CommentSaveRequest request = new CommentSaveRequest("contents");
        authUser = new AuthUser(1L, "email", UserRole.USER);
        user = User.fromAuthUser(authUser);
        todo = new Todo("title", "title", "contents", user);
        comment = new Comment(request.getContents(), user, todo);
        ReflectionTestUtils.setField(comment, "id", 1L);
    }

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;

        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.saveComment(authUser, todoId, request);
        });

        // then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }

    @Test
    void getCommentsTest(){
        Long id = 1L;
        List<Comment> commentList = List.of(comment);
        when(commentRepository.findByTodoIdWithUser(any(Long.class))).thenReturn(commentList);

        List<CommentResponse> responses = commentService.getComments(id);

        assertEquals(1,responses.size());
        CommentResponse response = responses.get(0);
        assertThat(response.getId()).isEqualTo(comment.getId());
        assertThat(response.getContents()).isEqualTo(comment.getContents());
        assertThat(response.getUser().getId()).isEqualTo(comment.getUser().getId());

    }
}
