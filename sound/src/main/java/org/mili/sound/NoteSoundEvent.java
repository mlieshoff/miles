package org.mili.sound;

/**
 * @author Michael Lieshoff
 */
public class NoteSoundEvent extends SoundEvent {

    private final char key;
    private final long duration;

    public NoteSoundEvent(char key, long duration) {
        this.key = key;
        this.duration = duration;
    }

    public char getKey() {
        return key;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Note " + key + " -> " + duration;
    }

}
