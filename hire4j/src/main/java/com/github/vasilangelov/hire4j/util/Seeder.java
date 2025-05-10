package com.github.vasilangelov.hire4j.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public abstract class Seeder implements ApplicationRunner {

    protected abstract void seed();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.seed();
    }

}
