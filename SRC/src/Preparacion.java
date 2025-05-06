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
        while (gestor.getPreparados() < 500) {                                                                                          //Ejecuto mientras que no se llegue al maximo de pedidos
            try {
                System.out.println("Preparando pedido " + gestor.getPreparados());
                PrepararPedido();                                                                                                       // hace lo necesario para preparar
                DormirProceso();                                                                                                        // simula la demora
                gestor.addPreparados();

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
        int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1); //Aca lo que hacemos es generar un numero random entre una cota inferior
        Thread.sleep(demora);                                                          //y una superior como nos pide el problema
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
