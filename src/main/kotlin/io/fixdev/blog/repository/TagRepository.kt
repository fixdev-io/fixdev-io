package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
    fun findBySlug(slug: String): Optional<Tag>
}
