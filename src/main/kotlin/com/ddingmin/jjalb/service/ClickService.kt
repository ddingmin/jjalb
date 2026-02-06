package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.Click
import com.ddingmin.jjalb.domain.ClickEvent
import com.ddingmin.jjalb.repository.ClickRepository
import com.ddingmin.jjalb.repository.LinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class ClickService(
    private val clickRepository: ClickRepository,
    private val linkRepository: LinkRepository
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    @EventListener
    fun handle(event: ClickEvent) {
        scope.launch {
            record(event.code, event.referrer, event.userAgent)
        }
    }

    suspend fun record(code: String, referrer: String?, userAgent: String?) {
        val link = linkRepository.findByCode(code) ?: return
        clickRepository.save(Click.of(link, referrer, userAgent))
    }
}
