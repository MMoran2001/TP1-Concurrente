import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Verificacion extends Thread{
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random RandoM = new Random();
    private final Semaphore semaforo;

    public Verificacion(int tiempoMin, int tiempoMax, Semaphore semaforo) {
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        this.semaforo = semaforo;
    }
    @Override
    public void run(){
        while (true) {
            try{
                    if(!semaforo.tryAcquire()){
                        break;
                    }
                    int indice = PedidoRandomEntregado();
                    if(indice==-1){semaforo.release();break;}
                        boolean verificacionExitosa = RandoM.nextInt(100) < 95;
                            if(verificacionExitosa){
                                Gestor.modificarRegistro(Gestor.getPedEntregado(),"ELIMINAR");
                                Gestor.modificarRegistro(Gestor.getPedVerificado(),"AGREGAR");
                                System.out.println("Pedido Verificado");
                            }
                            else{
                                Gestor.modificarRegistro(Gestor.getPedEntregado(),"ELIMINAR");
                                Gestor.modificarRegistro(Gestor.getPedFallido(),"AGREGAR");
                                System.out.println("Pedido No Verificado");
                            }
               DormirHilo();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    private int PedidoRandomEntregado() {
        synchronized (Gestor.getPedEntregado()) {                                          //Accedo a la lista de pedidos en transito
            if (Gestor.getPedEntregado().getContador() <= 0) {                             //si el contador es menor a 0.
                return -1;                                                              // No hay pedidos en tránsito
            }
            return RandoM.nextInt(Gestor.getPedEntregado().getContador());
        }
    }

    private void DormirHilo() {
        try {
             int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
              Thread.sleep(demora);
            }catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
    }
}

