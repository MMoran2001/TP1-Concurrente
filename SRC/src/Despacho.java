// Source code is decompiled from a .class file using FernFlower decompiler.
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Despacho extends Thread {
    private final Gestor gestor = Gestor.getMiGestor();
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random Random = new Random();

    public Despacho(int var1, int var2) {
        this.tiempoMin = var1;
        this.tiempoMax = var2;
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

                if (this.gestor.incrementarDespachadosSiPosible()) {
                    int[] var1 = this.buscarCasilleroOcupado();
                    int var2 = var1[0];
                    int var3 = var1[1];
                    boolean var4 = this.Random.nextInt(100) <= 85;
                    synchronized(this.gestor.getAlmacen()[var2][var3]) {
                        if (var4) {
                            this.gestor.getAlmacen()[var2][var3].cambiarEstado(Estado_Casilleros.VACIO);
                            this.gestor.modificarRegistro(this.gestor.getPedEnPrep(), "ELIMINAR");
                            this.gestor.modificarRegistro(this.gestor.getPedEnTran(), "AGREGAR");
                        } else {
                            this.gestor.getAlmacen()[var2][var3].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
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
            } catch (Exception var13) {
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
            int var1 = ThreadLocalRandom.current().nextInt(this.tiempoMin, this.tiempoMax + 1);
            Thread.sleep((long)var1);
        } catch (InterruptedException var2) {
            Thread.currentThread().interrupt();
        }

    }

    private int[] buscarCasilleroOcupado() {
        Casillero[][] var1 = this.gestor.getAlmacen();
        int[] var2 = this.gestor.randomPos();

        while(var1[var2[0]][var2[1]].getEstado() != Estado_Casilleros.OCUPADO) {
            synchronized(var1[var2[0]][var2[1]]) {
                var2 = this.gestor.randomPos();
            }
        }

        return var2;
    }
}
