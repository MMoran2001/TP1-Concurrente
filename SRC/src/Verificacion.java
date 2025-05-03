import java.util.Random;
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
                    synchronized (gestor.getMonitorVerificacion()){
                        gestor.getMonitorVerificacion().wait();
                    }
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
    private void DormirHilo() {
        try {
             int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
              Thread.sleep(demora);
            }catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
    }
}

