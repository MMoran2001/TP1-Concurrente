import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho extends Thread {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random RandoM = new Random();
    private final Semaphore semaforo;

    public Despacho(int tiempoMin, int tiempoMax, Semaphore semaforo) {
        this.semaforo = new Semaphore(500);
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    @Override
    public void run() {
        while (true) {
            try{
                if (!semaforo.tryAcquire()) {
                    break; // ya no hay más permisos → se prepararon todos los pedidos
                }
                int[] pos = buscarCasilleroOcupado();

                if (pos == null) {
                    DormirHilo();
                    continue; // no hay casilleros ocupados, vuelvo a intentar
                }

                    int i = pos[0];
                    int j = pos[1];

                boolean verificacionExitosa = RandoM.nextInt(100) < 85; // 85% de probabilidad

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
            } catch (Exception e) {
                Thread.currentThread().interrupt();                                     //Si se da la excepcion salgo del bucle
                break;
            }
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
        Casillero[][] almacen = gestor.getAlmacen();    //cremos un almacen

        for (int i=0; i<almacen.length; i++) {
            for (int j=0; j<almacen[i].length; j++) {
                if (almacen[i][j].getEstado() == Estado_Casilleros.OCUPADO) {   //Pregunto si esta ocupado
                    return new int[]{i, j};                     //si esta ocupado busco un nuevo lugar en la matriz
                }
            }
        }
        return null; // no encontró ninguno
    }
}
