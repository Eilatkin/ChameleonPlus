package com.eilatkin.ch_plus.evaluate;

import org.apache.commons.lang3.RandomStringUtils;

public enum PasswordChars {
    special ("~!?@#$%^&*_-+()[]{}></\\|\"'.,:;="),
    alphabeticEN ("abcdefghijklmnopqrstuvwxyz"),
    capitalAlphabeticEN ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    alphabeticRU ("абвгдеёжзийклмнопрстуфхцчшщъыьэюя"),
    capitalAlphabeticRU ("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"),
    numeric ("1234567890");

    private final String characters;

    public String getRandomCharacter() {
        return RandomStringUtils.random(1,characters);
    }

    public String getCharacters() {
        return characters;
    }

    public String insertCharRandomly(String before) {
        StringBuilder s = new StringBuilder(before);
        int offset = (int) (Math.random() * before.length());
        return s.insert(offset, this.getRandomCharacter()).toString();
    }

    PasswordChars(String characters) {
        this.characters = characters;
    }
}