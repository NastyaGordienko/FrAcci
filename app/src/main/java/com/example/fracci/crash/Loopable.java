package com.example.fracci.crash;

public interface Loopable {
    void cycle() throws InterruptedException;
    void stop();
}
