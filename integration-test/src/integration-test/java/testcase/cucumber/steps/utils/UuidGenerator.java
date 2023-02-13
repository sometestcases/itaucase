package testcase.cucumber.steps.utils;

import java.util.UUID;

public class UuidGenerator {

    private UuidGenerator() {

    }

    public static String generate() {
        return generate("");
    }

    public static String generate(String initials) {
        return (initials + UUID.randomUUID())
                .replace("-", "").toUpperCase()
                .substring(0, 32);
    }

}
