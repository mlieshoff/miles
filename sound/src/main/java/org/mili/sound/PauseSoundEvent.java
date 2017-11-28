package org.mili.sound;

/**
 * @author Michael Lieshoff
 */
public class PauseSoundEvent extends SoundEvent {

    private final long pause;

    public PauseSoundEvent(long pause) {
        this.pause = pause;
    }

    public long getPause() {
        return pause;
    }

    @Override
    public String toString() {
        return "Pause " + getPause();
    }

}
