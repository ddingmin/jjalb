package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.ShortCode

interface CodeGenerator {
    fun generate(id: Long): ShortCode
}
