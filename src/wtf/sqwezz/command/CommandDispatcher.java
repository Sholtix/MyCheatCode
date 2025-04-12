package wtf.sqwezz.command;

import wtf.sqwezz.command.impl.DispatchResult;

public interface CommandDispatcher {
    DispatchResult dispatch(String command);
}
