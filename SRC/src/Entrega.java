import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Entrega implements Runnable {
    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random random = new Random();
    private final AtomicInteger contador;

    public Entrega(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        this.contador = new AtomicInteger(0);
    }

    @Override
    public void run() {
        while (!gestor.isEntregaDone()) {
            try {
                synchronized (gestor.getMonitorEntrega()) {
                    while (gestor.getPedEnTran().getListaPedidos().isEmpty() && !gestor.isDespachoDone()) {
                        gestor.getMonitorEntrega().wait();
                    }
                }

                if (gestor.getPedEnTran().getListaPedidos().isEmpty() && gestor.isDespachoDone()) {
                    gestor.markEntregaDone();
                    System.out.println("FIN DE ENTREGA");
                    synchronized (gestor.getMonitorVerificacion()) {
                        gestor.getMonitorVerificacion().notify();
                    }
                    break;
                }


                boolean EntregaExitosa = random.nextInt(100) < 90;

                synchronized (gestor.getPedEnTran()) {
                    //if (!gestor.getPedEnTran().getListaPedidos().isEmpty()) {
                        gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");
                        if (EntregaExitosa) {
                            gestor.modificarRegistro(gestor.getPedEntregado(), "AGREGAR");
                            gestor.addEntregados();
                            synchronized (gestor.getMonitorVerificacion()) {
                                gestor.getMonitorVerificacion().notify();
                            }
                        } else {
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            contador.incrementAndGet();
                        }
                    //}
                }
                DormirHilo();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
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
}


