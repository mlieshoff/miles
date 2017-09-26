package org.mili.devtool;

import org.mili.utils.ApplicationData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class Main {

    private JFrame frame = new JFrame("DevTool V0.2");
    private JTextArea content = new JTextArea();
    private JTextField searchField = new JTextField();
    private Connector connector;
    private ResultTableModel resultTableModel;
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private Main() throws Exception {
        connector = new DefaultConnector(ApplicationData.create("devtool"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocation(2000, 50);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5));
        panel.add(createSearchPanel());
        panel.add(createContentPanel());
        frame.add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    private Component createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));

        panel.add("North", createSearchControlPanel());
        panel.add("Center", createResultPanel());

        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    private Component createSearchControlPanel() {
        searchField.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resultTableModel.refresh(doSearch(searchField.getText()));
                    resultTableModel.fireTableDataChanged();
                    frame.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setLayout(new BorderLayout(5, 5));
        panel.add("Center", searchField);
        panel.add("East", createSearchButton());
        return panel;
    }

    private Component createSearchButton() {
        JButton button = new JButton("Search");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTableModel.refresh(doSearch(searchField.getText()));
                resultTableModel.fireTableDataChanged();
                frame.repaint();
            }
        });
        return button;
    }

    private Component createResultPanel() {
        resultTableModel = new ResultTableModel(doSearch(null));
        final JTable table = new JTable(resultTableModel);
        table.setShowGrid(true);

        ListSelectionModel lsm = table.getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                if (e.getValueIsAdjusting() || Math.abs(first - last) == 1) {
                    int index = table.getSelectedRow();
                    Entry entry = resultTableModel.getEntry(index);
                    content.setText(entry == null ? "" : entry.getContent());
                }
            }
        });
        table.setSelectionModel(lsm);
        lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    private Component createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));

        panel.add("North", createContentControlPanel());
        panel.add("Center", content);

        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    private Component createContentControlPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setLayout(new BorderLayout(5, 5));
        panel.add("East", createCopyButton());
        return panel;
    }

    private Component createCopyButton() {
        JButton button = new JButton("Copy to clipboard");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboard.setContents(new StringSelection(content.getText()), null);
            }
        });
        return button;
    }

    private List<Entry> doSearch(String query) {
        return connector.search(query);
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
