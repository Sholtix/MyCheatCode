package wtf.sqwezz.command.impl;

import wtf.sqwezz.command.Parameters;
import wtf.sqwezz.command.ParametersFactory;

import java.util.Arrays;

public class ParametersFactoryImpl implements ParametersFactory {

    @Override
    public Parameters createParameters(String message, String delimiter) {
        return new ParametersImpl(message.split(delimiter));
    }
}
