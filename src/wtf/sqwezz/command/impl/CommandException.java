package wtf.sqwezz.command.impl;

import lombok.Value;

@Value
public class CommandException extends RuntimeException {
    String message;
}
