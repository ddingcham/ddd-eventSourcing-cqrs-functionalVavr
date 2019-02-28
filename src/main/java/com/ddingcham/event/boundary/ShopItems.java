package com.ddingcham.event.boundary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ShopItems {

    public void log() {
        log.debug("simple log");
    }
}
