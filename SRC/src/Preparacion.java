import java.util.concurrent.ThreadLocalRandom;

public class Preparacion extends Thread {
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
        while (true) {
            try {
                int resultado = gestor.procesarPedido("PREPARADO");
                if (resultado == -1) {
                    break;
                }
                System.out.println("Preparando pedido " + resultado);
                PrepararPedido();
                DormirProceso();

                synchronized (gestor.getMonitorDespacho()) {
                    gestor.getMonitorDespacho().notify();
                }
            } catch (InterruptedException e) {
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
        while (!pedidoTomado) {
            int[] pos = gestor.randomPos();
            pedidoTomado = gestor.TomarPedido(pos[0], pos[1]);
            if (pedidoTomado) {
                try {
                    DormirProceso();
                } catch (InterruptedException e) {
                    System.out.println("Me interrumpieron!");
                }
            }
        }
    }
}

