import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Verificacion extends Thread{
    private final Gestor gestor;
    private final int tiempoMin;
    private final int tiempoMax;
    private final Random random = new Random();

    public Verificacion(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
    }
    @Override
    public void run(){
        while (true) {
            try{
                    if(gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador() >= 500) {
                        break;
                    }
                    if(gestor.getPedEntregado().getContador() > 0){
                        boolean verificacionExitosa = random.nextInt(100) < 95;
                            if(verificacionExitosa){
                                gestor.modificarRegistro(gestor.getPedEntregado(),"ELIMINAR");
                                gestor.modificarRegistro(gestor.getPedVerificado(),"AGREGAR");
                                System.out.println("Pedido Verificado");
                            }
                            else{
                                gestor.modificarRegistro(gestor.getPedEntregado(),"ELIMINAR");
                                gestor.modificarRegistro(gestor.getPedFallido(),"AGREGAR");
                                System.out.println("Pedido No Verificado");
                            }
                    }
               DormirHilo();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    private int PedidoRandomEntregado() {
        synchronized (gestor.getPedEntregado()) {                                          //Accedo a la lista de pedidos en transito
            if (gestor.getPedEntregado().getContador() <= 0) {                             //si el contador es menor a 0.
                return -1;                                                              // No hay pedidos en tránsito
            }
            return random.nextInt(gestor.getPedEntregado().getContador());
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

