import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Verificacion implements Runnable {
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
        while (!gestor.isVerificacionDone()) {

            try {
                System.out.println("Verificando pedido " + gestor.getPedVerificado().getContador() + " quedan verificar " + (500 - (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador())));
                synchronized (gestor.getMonitorVerificacion()) {
                    while (gestor.getPedEntregado().getListaPedidos().isEmpty() && !gestor.isEntregaDone()) {
                        gestor.getMonitorVerificacion().wait();
                    }
                    if (gestor.getPedEntregado().getListaPedidos().isEmpty() && gestor.isEntregaDone()) {
                        System.out.println("Cantidad de pedidos en preparacion cuando ya termino verificacion:" + gestor.getPedEnPrep().getContador() + " o:?+" + gestor.getPedEnPrep().getListaPedidos().size());
                        gestor.markVerificacionDone();
                    }
                }
                if ((gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador() >= 500) && gestor.isVerificacionDone()) {
                    System.out.println("FIN DE VERIFICACION");
                    break;
                }
                synchronized (gestor.getPedEntregado()) {
                    System.out.println("estoy laburando");
                    if (!(gestor.getPedEntregado().getListaPedidos().isEmpty())) {
                        boolean verificacionExitosa = random.nextInt(100) < 95;
                        gestor.modificarRegistro(gestor.getPedEntregado(), "ELIMINAR");
                        if (verificacionExitosa) {
                            gestor.modificarRegistro(gestor.getPedVerificado(), "AGREGAR");
                        } else {
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                        }
                        System.out.println("La cantidad de pedidos verificados es:" + gestor.getPedVerificado().getContador());
                        System.out.println("La cantidad de pedidos fallidos es:" + gestor.getPedFallido().getContador());
                    }else{
                        //gestor.markVerificacionDone();
                        //break;
                    }
                }

                DormirHilo();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("FIN DE VERIFICACION");
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
//((gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador() < 500))&&
