package io.fixdev.blog.service

import io.fixdev.blog.model.entity.*
import io.fixdev.blog.repository.CommentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class CommentServiceTest {
    @Mock lateinit var commentRepository: CommentRepository
    @InjectMocks lateinit var commentService: CommentService

    @Test
    fun `create saves comment with PENDING status`() {
        val article = Article(id = 1, title = "Test", slug = "test")
        val user = User(id = 1, keycloakSub = "sub", name = "User")
        whenever(commentRepository.save(any<Comment>())).thenAnswer { it.arguments[0] }

        val comment = commentService.create(article, user, "Great article!")

        assertThat(comment.status).isEqualTo(CommentStatus.PENDING)
        assertThat(comment.text).isEqualTo("Great article!")
    }

    @Test
    fun `approve sets status to APPROVED`() {
        val article = Article(id = 1, title = "Test", slug = "test")
        val user = User(id = 1, keycloakSub = "sub", name = "User")
        val comment = Comment(id = 1, article = article, user = user, text = "Hi")
        whenever(commentRepository.findById(1L)).thenReturn(java.util.Optional.of(comment))
        whenever(commentRepository.save(any<Comment>())).thenAnswer { it.arguments[0] }

        val result = commentService.approve(1L)

        assertThat(result.status).isEqualTo(CommentStatus.APPROVED)
    }
}
