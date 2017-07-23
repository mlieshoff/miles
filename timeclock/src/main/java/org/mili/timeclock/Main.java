package org.mili.timeclock;

import org.apache.commons.io.FileUtils;
import org.mili.utils.ApplicationData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michael Lieshoff, 28.03.17
 */
public class Main implements ActionListener {

    static final File DIR = ApplicationData.create("timeclock");

    private JLabel timeLabel = new JLabel();
    private JLabel perfLabel = new JLabel();

    private JButton inButton = new JButton("IN");
    private JButton breakButton = new JButton("BRK");
    private JButton outButton = new JButton("OUT");

    public Main() throws ClassNotFoundException, SQLException {
        inButton.addActionListener(this);
        breakButton.addActionListener(this);
        outButton.addActionListener(this);

        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        timeLabel.setForeground(Color.GREEN);
        timeLabel.setBackground(Color.BLACK);

        perfLabel.setHorizontalAlignment(SwingConstants.CENTER);
        perfLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        perfLabel.setForeground(Color.GREEN);
        perfLabel.setBackground(Color.BLACK);

        JPanel panel = new JPanel();
        panel.setSize(240, 100);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.add(perfLabel, BorderLayout.NORTH);
        panel.add(timeLabel, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(1, 3, 5, 5));
        controls.add(inButton);
        controls.add(breakButton);
        controls.add(outButton);
        controls.setBackground(Color.BLACK);

        panel.add(controls, BorderLayout.SOUTH);

        JFrame frame = new JFrame("TimeClock");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(240, 100));
        frame.setLocation(2000, 50);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    updateTimeLabel();
                    try {
                        Thread.sleep(1000 - (System.currentTimeMillis() % 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        Thread minThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    updatePerfLabel();
                    try {
                        Thread.sleep(60000 - (System.currentTimeMillis() % 60000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        minThread.start();
    }

    private void updateTimeLabel() {
        timeLabel.setText(new SimpleDateFormat().format(new Date()));
    }

    private void updatePerfLabel() {
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        File file = new File(DIR, "timeclock-" + date + ".csv");
        try {
            java.util.List<String> data = Report.process(date, file);
            perfLabel.setText(String.format("IN: %s Work: %s Break: %s", data.get(3), data.get(1), data.get(2)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Date date = new Date();
        if (e.getSource() == inButton) {
            addCsvLine(date, "IN");
        } else if (e.getSource() == outButton) {
            addCsvLine(date, "OUT");
        } else if (e.getSource() == breakButton) {
            addCsvLine(date, "BRK");
        }
    }

    private void addCsvLine(Date date, String action) {
        File file = new File(DIR, "timeclock-" + new SimpleDateFormat("yyMMdd").format(date) + ".csv");
        StringBuilder line = new StringBuilder();
        line.append(String.format("\"%s\"", new SimpleDateFormat("HHmmss").format(date)));
        line.append(",");
        line.append(String.format("\"%s\"", action));
        line.append(System.lineSeparator());
        try {
            FileUtils.write(file, line.toString(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
