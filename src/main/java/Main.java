import student.crazyeights.ConsoleUI;
import student.crazyeights.Tournament;

import java.util.Random;


public class Main {
    public static void main(String[] args) {
        Random random = new Random();
        ConsoleUI ui = new ConsoleUI(System.in, System.out);
        Tournament tournament = new Tournament(ui, random);
        tournament.startTournament();
    }
}