package ru.yandex.practicum.filmorate.testUtils;

import net.bytebuddy.utility.RandomString;

import java.util.Random;

public class DataGenerator {

    public static String generateStringWithLength(int length){
        return RandomString.make(length);
    }
}
