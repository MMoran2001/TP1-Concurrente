import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Entrega extends Thread{
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random RandoM = new Random();
    private final Semaphore semaforo;

    public Entrega(Gestor gestor, int tiempoMin, int tiempoMax, Semaphore semaforo) {
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        this.semaforo = semaforo;
    }
    @Override
    public void run(){
        while (true){
            try {
                if (!semaforo.tryAcquire()) {
                    break;
                }
                    int indice = pedidoAleatorio();                                                           //Tomo el pedido aleatorio
                        if (indice == -1) {                                                                   //si el indice es menor a 0
                            semaforo.release();                                                               //Libero 1 permiso de semaforo
                            continue;
                        }
                boolean EntregaExitosa = RandoM.nextInt(100) < 90;                                     //Probabilidad del 90%
                    if (EntregaExitosa) {
                        Gestor.modificarRegistro(Gestor.getPedEnTran(), "ELIMINAR");                //Elimino al registro de pedidos en Transito
                        Gestor.modificarRegistro(Gestor.getPedEntregado(), "AGREGAR");              //Agrego al registro de pedidos entregados
                        System.out.println("Entregado");                                                     //Para ver que ande
                    } else {
                        Gestor.modificarRegistro(Gestor.getPedEnTran(), "ELIMINAR");               //Elimino al registro de pedidos en transito
                        Gestor.modificarRegistro(Gestor.getPedFallido(), "AGREGAR");               //Agrego al registro de pedidos fallidos
                        System.out.println("Fallido");                                                      //Para ver que ande
                    }
                    DormirHilo();
            }catch (Exception e) {
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
        synchronized (Gestor.getPedEnTran()) {                                          //Accedo a la lista de pedidos en transito
            if (Gestor.getPedEnTran().getContador() <= 0) {                             //si el contador es menor a 0.
                return -1;                                                              // No hay pedidos en tránsito
            }
            return RandoM.nextInt(Gestor.getPedEnTran().getContador());                 // Devuelve un índice aleatorio
        }
    }
}
