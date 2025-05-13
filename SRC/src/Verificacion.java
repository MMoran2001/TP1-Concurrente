import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Verificacion extends Thread {
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
    public void run() {
        while (true) {

            try {
                System.out.println("Verificando pedido " + gestor.getPedVerificado().getContador() + " quedan verificar " + (500 - (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador())));
                synchronized (gestor.getMonitorVerificacion()) {
                    while (gestor.getPedEntregado().getContador() == 0 && !gestor.isEntregaDone()) {
                        gestor.getMonitorVerificacion().wait();
                    }
                    if (gestor.getPedEntregado().getContador() == 0 && gestor.isEntregaDone()) {
                        gestor.markVerificacionDone();
                    }
                }

                synchronized (gestor.getPedEntregado()) {
                    if (gestor.getPedEntregado().getContador() > 0) {
                        boolean verificacionExitosa = random.nextInt(100) < 95;
                        gestor.modificarRegistro(gestor.getPedEntregado(), "ELIMINAR");
                        if (verificacionExitosa) {
                            gestor.modificarRegistro(gestor.getPedVerificado(), "AGREGAR");
                            //System.out.println("Pedido Verificado");
                        } else {
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            //System.out.println("Pedido No Verificado");
                        }
                    }
                }
                if ((gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador() >= 500) && gestor.isVerificacionDone()) {
                    System.out.println("FIN DE VERIFICACION");
                    break;
                }
                DormirHilo();
            } catch (InterruptedException e) {
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

