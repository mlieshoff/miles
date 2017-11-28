package org.mili.sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Michael Lieshoff, 28.03.17
 */
public class Main implements ActionListener, KeyListener {

//    static final File DIR = ApplicationData.create("sound");

    private enum Mode {
        RECORD,
        NONE;
    }

    private JButton recordButton = new JButton("RECORD");
    private JButton stopButton = new JButton("STOP");
    private JButton playButton = new JButton("PLAY");

    private Mode mode = Mode.NONE;

    public Main() throws ClassNotFoundException, SQLException {
        cacheSounds();

        recordButton.addActionListener(this);
        stopButton.addActionListener(this);
        playButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setSize(240, 100);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(1, 3, 5, 5));
        controls.add(recordButton);
        controls.add(stopButton);
        controls.add(playButton);
        controls.setBackground(Color.BLACK);

        panel.add(controls, BorderLayout.SOUTH);

        JFrame frame = new JFrame("SoundEdit");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(240, 100));
        frame.setLocation(2000, 50);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        recordButton.addKeyListener(this);
    }

    private void cacheSounds() {
        File dir = new File("./sounds");
        for (File file : dir.listFiles()) {
            cachePlayer(file);
        }
    }

    private void cachePlayer(File file) {
        /*
        System.out.println("play sound " + number);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File("/home/micha/dev/testwiese/sounds/" + number + ".mp3"));
            AdvancedPlayer player = new AdvancedPlayer(
                    inputStream,
                    FactoryRegistry.systemRegistry().createAudioDevice()
            );
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == recordButton) {
            soundEvents.clear();
            notePauseStart = System.currentTimeMillis();
            mode = Mode.RECORD;
        } else if (e.getSource() == stopButton) {
            mode = Mode.NONE;
            for (SoundEvent soundEvent : soundEvents) {
                System.out.println(soundEvent);
            }
        } else if (e.getSource() == playButton) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (SoundEvent soundEvent : soundEvents) {
                        if (soundEvent instanceof PauseSoundEvent) {
                            PauseSoundEvent pauseSoundEvent = (PauseSoundEvent) soundEvent;
                            System.out.println(pauseSoundEvent);
                            try {
                                Thread.sleep(pauseSoundEvent.getPause());
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } else if (soundEvent instanceof NoteSoundEvent) {
                            NoteSoundEvent noteSoundEvent = (NoteSoundEvent) soundEvent;
                            System.out.println(noteSoundEvent);
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private long notePauseStart = 0;
    private long notePauseEnd = 0;
    private long notePause = 0;
    private long noteStart = 0;
    private long noteEnd = 0;
    private long noteDuration = 0;

    private final java.util.List<SoundEvent> soundEvents = new ArrayList<>();

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (noteStart == 0) {
            if (notePauseStart > 0) {
                notePauseEnd = System.currentTimeMillis();
                notePause = notePauseEnd - notePauseStart;
                soundEvents.add(new PauseSoundEvent(notePause));
                notePauseStart = 0;
                notePauseEnd = 0;
                notePause = 0;
            }
            noteStart = System.currentTimeMillis();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (noteStart > 0) {
            noteEnd = System.currentTimeMillis();
            noteDuration = noteEnd - noteStart;
            soundEvents.add(new NoteSoundEvent(keyEvent.getKeyChar(), noteDuration));
            noteStart = 0;
            noteEnd = 0;
            noteDuration = 0;
            notePauseStart = System.currentTimeMillis();
        }
    }

}
