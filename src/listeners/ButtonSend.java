package listeners;

import models.Database;
import models.Model;
import views.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonSend implements  ActionListener{
    private final Model model;
    private final View view;

    public ButtonSend(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    private void win () {
        String currentDate = view.getRealTimer().getDate();
        String playerName = JOptionPane.showInputDialog(null, "Palun siseta teie nimi:", "Palju õnne, sa võitsid!", JOptionPane.QUESTION_MESSAGE);
        if (playerName != null && !playerName.trim().isEmpty()) {
            //System.out.println(playerName);
        } else {
            JOptionPane.showMessageDialog(null, "Nimi ei saa olla tühi!");
            playerName = JOptionPane.showInputDialog(null, "Palun siseta teie nimi:", "Palju õnne, sa võitsid!", JOptionPane.QUESTION_MESSAGE);
        }
        String playedTime = String.valueOf(view.getGameTimer().getPlayedTimeInSeconds());
        new Database(model).sendToTable(currentDate, playerName, model.getWord(), String.valueOf(model.getWrongLetters()), playedTime);

    }

    private void lost () {
        JOptionPane.showMessageDialog(null, "Sa kaotasid.");
    }
    private void endGame() {
        view.showButtons();
        view.getGameTimer().setRunning(false);
        view.getGameTimer().stopTime();
        view.getGameBoard().clearGameBoard();
        view.getLeaderBoard().updateScoresTable();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String character = view.getGameBoard().getTxtChar().getText().toLowerCase();
        if (character.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Palun siseta täht!");
        } else {
            if (model.getWord().contains(character)) {
                view.updateLblResult(character);
            } else {
                model.setMistakes(model.getMistakes() + 1);
                model.addLetter(character);
                view.updateLblImage(model.getMistakes());
                view.getGameBoard().getLblError().setText("Vigased tähed: " + model.getWrongLetters());
            }
        }
        if (model.getWord().equals(model.getGuessedWord())) {
            view.getGameTimer().stopTime();
            win();
            endGame();
        }
        if (model.getMistakes() > 10) {
            lost();
            endGame();
        }
        view.getGameBoard().getTxtChar().setText("");
        view.getGameBoard().getTxtChar().requestFocusInWindow();

        //System.out.println(model.getWord());
    }
}
