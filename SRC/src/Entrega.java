import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Entrega extends Thread{
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

            while (true) {

                try {
                    synchronized (gestor.getMonitorEntrega()) {
                        while (gestor.getPedEnTran().getContador() == 0 && !gestor.isDespachoDone()) {
                            gestor.getMonitorEntrega().wait();
                        }
                    }
                    if (gestor.getPedEnTran().getContador() == 0 && gestor.isDespachoDone()) {
                        gestor.markEntregaDone();
                        System.out.println("FIN DE ENTREGA");
                        synchronized (gestor.getMonitorVerificacion()) {
                            gestor.getMonitorVerificacion().notifyAll();
                        }
                        break;
                    }
                    int indice = pedidoAleatorio();                                                           //Tomo el pedido aleatorio
                    if (indice != -1) {                                                                   //si el indice es menor a 0
                        boolean EntregaExitosa = random.nextInt(100) < 90;                                    //Probabilidad del 90%
                        if (EntregaExitosa) {
                            gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");                //Elimino al registro de pedidos en Transito
                            gestor.modificarRegistro(gestor.getPedEntregado(), "AGREGAR");
                            gestor.addEntregados();                                                          //Agrego al registro de pedidos entregados
                            System.out.println("Entregado");                                                     //Para ver que ande

                        } else {
                            gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");               //Elimino al registro de pedidos en transito
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            contador.incrementAndGet();                                                         //Agrego al registro de pedidos fallidos
                            System.out.println("Fallido");                                                      //Para ver que ande
                        }
                        synchronized (gestor.getMonitorVerificacion()){
                            gestor.getMonitorVerificacion().notifyAll();
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
    private int pedidoAleatorio() {
        synchronized (gestor.getPedEnTran()) {                                          //Accedo a la lista de pedidos en transito
            if (gestor.getPedEnTran().getContador() <= 0) {                             //si el contador es menor a 0.
                return -1;                                                              // No hay pedidos en tránsito
            }
            return random.nextInt(gestor.getPedEnTran().getContador());                 // Devuelve un índice aleatorio
        }
    }

}
