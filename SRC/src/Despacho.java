// Source code is decompiled from a .class file using FernFlower decompiler.
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho extends Thread {
    private final Gestor gestor = Gestor.getMiGestor();
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random Random = new Random();

    public Despacho(int tiempoMin, int tiempoMax) {
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Despachando pedido " + this.gestor.getDespachados());
                synchronized(this.gestor.getMonitorDespacho()) {
                    while(true) {
                        if (this.gestor.getPedEnPrep().getContador() != 0 || this.gestor.isPreparacionDone()) {
                            break;
                        }
                        this.gestor.getMonitorDespacho().wait();
                    }
                }

                int resultado = this.gestor.procesarPedido("DESPACHADO");
                if (resultado != -1) {
                    int[] pos = this.buscarCasilleroOcupado();
                    boolean exitoso = this.Random.nextInt(100) <= 85;
                    synchronized(this.gestor.getAlmacen()[pos[0]][pos[1]]) {
                        if (exitoso) {
                            this.gestor.getAlmacen()[pos[0]][pos[1]].cambiarEstado(Estado_Casilleros.VACIO);
                            this.gestor.modificarRegistro(this.gestor.getPedEnPrep(), "ELIMINAR");
                            this.gestor.modificarRegistro(this.gestor.getPedEnTran(), "AGREGAR");
                        } else {
                            this.gestor.getAlmacen()[pos[0]][pos[1]].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                            this.gestor.modificarRegistro(this.gestor.getPedEnPrep(), "ELIMINAR");
                            this.gestor.modificarRegistro(this.gestor.getPedFallido(), "AGREGAR");
                        }
                    }

                    synchronized(this.gestor.getMonitorEntrega()) {
                        this.gestor.getMonitorEntrega().notify();
                    }

                    this.DormirHilo();
                    continue;
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("FIN DE DESPACHO");
            this.gestor.markDespachoDone();
            synchronized(this.gestor.getMonitorEntrega()) {
                this.gestor.getMonitorEntrega().notifyAll();
                return;
            }
        }
    }

    private void DormirHilo() {
        try {
            int demora = ThreadLocalRandom.current().nextInt(this.tiempoMin, this.tiempoMax + 1);
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int[] buscarCasilleroOcupado() {
        Casillero[][] almacen = this.gestor.getAlmacen();
        int[] pos = this.gestor.randomPos();

        while(almacen[pos[0]][pos[1]].getEstado() != Estado_Casilleros.OCUPADO) {
            synchronized(almacen[pos[0]][pos[1]]) {
                pos = this.gestor.randomPos();
            }
        }

        return pos;
    }
}
