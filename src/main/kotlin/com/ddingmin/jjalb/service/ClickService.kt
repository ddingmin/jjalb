package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.Click
import com.ddingmin.jjalb.domain.ClickEvent
import com.ddingmin.jjalb.repository.ClickRepository
import com.ddingmin.jjalb.repository.LinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class ClickService(
    private val clickRepository: ClickRepository,
    private val linkRepository: LinkRepository
) : DisposableBean {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @EventListener
    fun handle(event: ClickEvent) {
        scope.launch {
            try {
                record(event.code, event.referrer, event.userAgent)
            } catch (e: Exception) {
                logger.error("Failed to record click for code={}", event.code, e)
            }
        }
    }

    suspend fun record(code: String, referrer: String?, userAgent: String?) {
        val link = linkRepository.findByCode(code) ?: return
        clickRepository.save(Click.of(link, referrer, userAgent))
    }

    override fun destroy() {
        scope.cancel()
    }
}
