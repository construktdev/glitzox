package dev.construkter.glitzoxhelper.util;

public class SmartProgressBar {
    private final int total;
    private final int barLength;
    private int current = 0;
    private boolean initialized = false;
    private final String classn;

    public SmartProgressBar(int total, int barLength, String classn) {
        this.total = total;
        this.barLength = barLength;
        this.classn = classn;
    }

    /**
     * Initialisiert die Progressbar am unteren Bildschirmrand (eine Zeile).
     */
    private void init() {
        if (!initialized) { // Platz für Fortschrittsbalken schaffen
            initialized = true;
        }
    }

    /**
     * Gibt eine Nachricht über der Fortschrittsleiste aus.
     */
    public synchronized void log(String message) {
        init();
        // Eine Zeile hochgehen, Progressbar temporär verdecken
        System.out.print("\033[A");        // Cursor eine Zeile nach oben
        System.out.print("\033[K");        // Zeile löschen
        System.out.println(message);       // Nachricht ausgeben
        render();                          // Progressbar wieder darunter zeichnen
    }

    /**
     * Aktualisiert den Fortschritt.
     */
    public synchronized void update(int value) {
        init();
        this.current = Math.min(value, total);
        render();
    }

    /**
     * Zeichnet den Fortschrittsbalken neu.
     */
    private void render() {
        double percent = (double) current / total;
        int filled = (int) (percent * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled - 1) bar.append("=");
            else if (i == filled - 1) bar.append(">");
            else bar.append(" ");
        }
        bar.append("] - ");
        bar.append(String.format("%3d%%", (int) (percent * 100)));

        System.out.print("\033[K\r" + classn + " >>  " +  bar); // Zeile löschen, neu schreiben
        System.out.flush();
    }

    /**
     * Beendet die Fortschrittsanzeige.
     */
    public synchronized void finish() {
        update(total);
        System.out.println(); // Neue Zeile, Cursor bleibt unten
    }
}
