package com.bobocode.net.client;

public class ExerciseNotCompletedException extends Throwable {
    public ExerciseNotCompletedException() {
        super("Exercise is not completed");
    }
}
