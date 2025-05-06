import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Entrega extends Thread {
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
                System.out.println("Quedan despachar " + (500 - gestor.getDespachados()));
                synchronized (gestor.getMonitorEntrega()) {
                    while (gestor.getPedEnTran().getContador() == 0 && !gestor.isDespachoDone()) {
                        gestor.getMonitorEntrega().wait();
                    }
                }
                if (gestor.getPedEnTran().getContador() <= 0 && gestor.isDespachoDone()) {
                    gestor.markEntregaDone();
                    System.out.println("FIN DE ENTREGA");
                    synchronized (gestor.getMonitorVerificacion()) {
                        gestor.getMonitorVerificacion().notify();
                    }
                    continue;
                }
                //si el indice es menor a 0
                boolean EntregaExitosa = random.nextInt(100) < 90;                                    //Probabilidad del 90%

                synchronized (gestor.getPedEnTran()) {

                        gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");
                        if (EntregaExitosa) {
                            gestor.modificarRegistro(gestor.getPedEntregado(), "AGREGAR");
                            gestor.addEntregados();                                                          //Agrego al registro de pedidos entregados
                            synchronized (gestor.getMonitorVerificacion()) {
                                gestor.getMonitorVerificacion().notify();
                            }

                        } else {
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            contador.incrementAndGet();                                                         //Agrego al registro de pedidos fallidos

                        }

                }
                DormirHilo();
            } catch (Exception e) {
                Thread.currentThread().interrupt();                                                        //Si se da la excepcion salgo del bucle
                break;
            }
        }

    }

    private void DormirHilo() {                                                                           //Metodo que usamos para simular que el hilo de duerma
        try {
            int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
