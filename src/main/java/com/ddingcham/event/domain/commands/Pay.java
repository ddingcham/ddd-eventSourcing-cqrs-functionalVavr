package com.ddingcham.event.domain.commands;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class Pay implements Command {

    private final UUID uuid;
    private final Instant when;

}
