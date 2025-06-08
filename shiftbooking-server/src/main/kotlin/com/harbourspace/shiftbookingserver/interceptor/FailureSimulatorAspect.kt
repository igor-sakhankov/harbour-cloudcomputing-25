package com.harbourspace.shiftbookingserver.interceptor

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class FailureSimulatorAspect {
    @Around("@annotation(com.harbourspace.shiftbookingserver.interceptor.FailureSimulator)")
    fun simulate(joinPoint: ProceedingJoinPoint): Any? {
        val random = Math.random()
        return when {
            random < 0.5 -> joinPoint.proceed()
            random < 0.8 -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            else -> {
                Thread.sleep(1000)
                joinPoint.proceed()
            }
        }
    }
}
