import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho implements Runnable {

    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random Random = new Random();



    public Despacho(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    @Override
    public void run() {
        while (!gestor.isDespachoDone()) {
            try {

                synchronized (gestor.getMonitorDespacho()) {
                    while (gestor.getPedEnPrep().getListaPedidos().isEmpty() && !gestor.isPreparacionDone()) {
                        gestor.getMonitorDespacho().wait();
                    }
                }
                if(gestor.isPreparacionDone() && gestor.getPedEnPrep().getListaPedidos().isEmpty()) {
                    gestor.markDespachoDone();
                    break;
                }
                int[] pos = buscarCasilleroOcupado();
                int i = pos[0];
                int j = pos[1];

                boolean verificacionExitosa = Random.nextInt(100) <= 85;

                synchronized (gestor.getAlmacen()[i][j]) {
                    if(!gestor.getPedEnPrep().getListaPedidos().isEmpty() && gestor.getAlmacen()[i][j].getEstado()==Estado_Casilleros.OCUPADO) {
                        System.out.println("Despachando pedido " + gestor.getDespachados() ) ;
                        System.out.println("Quedan Despachar " + (gestor.getPreparados() - gestor.getDespachados()));
                        gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                        if (verificacionExitosa) {
                            gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.VACIO);
                            gestor.modificarRegistro(gestor.getPedEnTran(), "AGREGAR");
                        } else {
                            gestor.getAlmacen()[i][j].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            System.out.println("La cantidad de pedidos fallidos en despacho " + gestor.getPedFallido().getContador());
                        }
                        gestor.addDespachados();
                    }
                }

                synchronized (gestor.getMonitorEntrega()) {
                    gestor.getMonitorEntrega().notify();
                }

                DormirHilo();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error en el despacho");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("FIN DE DESPACHO");

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
