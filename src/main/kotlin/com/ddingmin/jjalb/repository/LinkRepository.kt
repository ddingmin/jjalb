package com.ddingmin.jjalb.repository

import com.ddingmin.jjalb.domain.Link
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LinkRepository : CoroutineCrudRepository<Link, Long> {
    suspend fun findByCode(code: String): Link?
    suspend fun findByAuthor(author: String): List<Link>
}
