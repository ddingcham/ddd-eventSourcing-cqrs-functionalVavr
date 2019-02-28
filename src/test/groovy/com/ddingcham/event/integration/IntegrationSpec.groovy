package com.ddingcham.event.integration

import com.ddingcham.event.Sample
import groovy.transform.CompileStatic
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Sample], loader = SpringApplicationContextLoader)
@CompileStatic
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ActiveProfiles("test")
class IntegrationSpec extends Specification {
}
