package com.ddingmin.jjalb.repository

import com.ddingmin.jjalb.domain.Click
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ClickRepository : CoroutineCrudRepository<Click, Long>
