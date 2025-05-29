import java.util.concurrent.ThreadLocalRandom;

public class Preparacion implements Runnable {
    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;

    public Preparacion(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }

    @Override
    public void run() {
        while (!gestor.isPreparacionDone()) {
            try {
//                if (gestor.getPreparados() >= 500) {
//                    gestor.markPreparacionDone();
//                    break;
//                }
                PrepararPedido();
                DormirProceso();

                synchronized (gestor.getMonitorDespacho()) {
                    gestor.getMonitorDespacho().notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }
        gestor.markPreparacionDone();
        synchronized (gestor.getMonitorDespacho()) {
            gestor.getMonitorDespacho().notifyAll();
        }
        System.out.println("FIN DE PREPARACION");
    }

    private void DormirProceso() throws InterruptedException {
        int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
        Thread.sleep(demora);
    }

    public void PrepararPedido() {
        boolean pedidoTomado = false;
        if(gestor.getPreparados() < 500) {
                int[] pos = gestor.randomPos();
                pedidoTomado = gestor.TomarPedido(pos[0], pos[1]);
                if (pedidoTomado) {
                    System.out.println("Preparando pedido " + gestor.getPreparados());
                    gestor.addPreparados();
                    try {
                        DormirProceso();
                    } catch (InterruptedException e) {
                        System.out.println("Me interrumpieron!");
                    }
                }
        }else{
            System.out.println("Se termino preparacion con: " + gestor.getPreparados() + " pedidos");
            gestor.markPreparacionDone();
        }

    }
}

