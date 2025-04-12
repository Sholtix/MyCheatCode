package wtf.sqwezz.command;

public interface CommandProvider {
    Command command(String alias);
}
