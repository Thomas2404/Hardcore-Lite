package me.thomas2404.hardcoreLite;

public class LifeWord {

    public LifeWord() {
    }

    public String getWord(int lives) {
        if (lives != 1) {
            return "lives.";
        } else {
            return "life.";
        }
    }
}
