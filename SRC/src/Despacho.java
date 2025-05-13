import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Despacho extends Thread {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random Random = new Random();
    private final AtomicInteger contador;


    public Despacho(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        this.contador = new AtomicInteger(0);
    }

    @Override
    public void run() {
        while (gestor.getDespachados() < 500) {
            try {
                System.out.println("Despachando pedido " + gestor.getDespachados());
                synchronized (gestor.getMonitorDespacho()) {
                    while (gestor.getPedEnPrep().getContador() == 0 && !gestor.isPreparacionDone()) {
                        gestor.getMonitorDespacho().wait();
                    }
                }
                int[] pos = buscarCasilleroOcupado();
                int i = pos[0];
                int j = pos[1];

                boolean verificacionExitosa = Random.nextInt(100) <= 85; // 85% de probabilidad

                synchronized (gestor.getAlmacen()[i][j]) {
                    if (verificacionExitosa) {
                        gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.VACIO);
                        gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                        gestor.modificarRegistro(gestor.getPedEnTran(), "AGREGAR");
                        gestor.aumentarContador();
                    } else {
                        gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                        gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                        gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                    }
                }
                synchronized (gestor.getMonitorEntrega()) {
                    gestor.getMonitorEntrega().notify();
                }
                gestor.addDespachados();
                DormirHilo();

            } catch (Exception e) {
                Thread.currentThread().interrupt();                                     //Si se da la excepcion salgo del bucle
                break;
            }
            if(gestor.getDespachados() == 500){
                System.out.println("FIN DE PREPARACION");
            }
        }
        //System.out.println("FIN DE DESPACHO");
        gestor.markDespachoDone();
        synchronized (gestor.getMonitorEntrega()) {
            gestor.getMonitorEntrega().notifyAll();
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
        Casillero[][] almacen = gestor.getAlmacen();    //Obtenemos el almacen
        int[] pos = gestor.randomPos();
        while (almacen[pos[0]][pos[1]].getEstado() != Estado_Casilleros.OCUPADO) {
            synchronized (almacen[pos[0]][pos[1]]) {
                pos = gestor.randomPos();
            }
        }
        return pos;
    }
}
