import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho extends Thread {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random random = new Random();

    public Despacho(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    @Override
    public void run() {
        while (true) {
            int[] pos = buscarCasilleroOcupado();

            if (pos == null) {
                DormirHilo();
                continue; // no hay casilleros ocupados, vuelvo a intentar
            }

            int i = pos[0];
            int j = pos[1];

            boolean verificacionExitosa = random.nextInt(100) < 85; // 85% de probabilidad

            synchronized (gestor.getAlmacen()[i][j]) {
                if (verificacionExitosa) {
                    gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.VACIO);
                    Gestor.modificarRegistro(Gestor.getPedEnPrep(), "ELIMINAR");
                    Gestor.modificarRegistro(Gestor.getPedEnTran(), "AGREGAR");
                } else {
                    gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                    Gestor.modificarRegistro(Gestor.getPedEnPrep(), "ELIMINAR");
                    Gestor.modificarRegistro(Gestor.getPedFallido(), "AGREGAR");
                }
            }

            DormirHilo();
        }
    }

    private void DormirHilo() {
        try {
            int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Busca un casillero que esté ocupado para simular que tiene un pedido en preparación
    private int[] buscarCasilleroOcupado() {
        Casillero[][] almacen = gestor.getAlmacen();

        for (int intento = 0; intento < 50; intento++) {
            int i = random.nextInt(10);
            int j = random.nextInt(20);
            if (almacen[i][j].getEstado() == Estado_Casilleros.OCUPADO) {
                return new int[]{i, j};
            }
        }
        return null; // no encontró ninguno
    }
}
