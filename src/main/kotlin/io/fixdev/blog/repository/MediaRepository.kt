package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Media
import org.springframework.data.jpa.repository.JpaRepository

interface MediaRepository : JpaRepository<Media, Long>
