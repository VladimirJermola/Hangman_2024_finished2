package listeners;

import models.Model;
import views.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonNew implements ActionListener {
    private Model model;
    private View view;
    public ButtonNew(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // System.out.println("Klikk"); // Test
        view.hideButtons();

        if(!view.getGameTimer().isRunning()) { // Mängu aeg ei kookse
            view.getGameTimer().setSeconds(0); // sekundid nullida
            view.getGameTimer().setMinutes(0); // minutid nullida
            view.getGameTimer().setRunning(true); // Aeg jooksma
            view.getGameTimer().startTime(); // Käivita aeg
        } else {
            view.getGameTimer().stopTime();
            view.getGameTimer().setRunning(false);

        }
        // TODO Siit jätkub õpilaste arendus
    }
}
