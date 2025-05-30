import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

// Clase que maneja la entrega de pedidos, extiende de Thread para ejecutarse concurrentemente
public class Entrega extends Thread {
    // Referencias y variables de instancia
    private final Gestor gestor;              // Referencia al gestor singleton
    private final int tiempoMin;              // Tiempo mínimo de demora para entregas
    private final int tiempoMax;              // Tiempo máximo de demora para entregas
    private final Random random = new Random(); // Generador de números aleatorios
    private final AtomicInteger contador;      // Contador atómico para pedidos fallidos

    // Constructor que inicializa los tiempos de entrega
    public Entrega(int tiempoMin, int tiempoMax) {
        this.gestor = Gestor.getMiGestor();
        this.tiempoMin = tiempoMin;
        this.tiempoMax = tiempoMax;
        this.contador = new AtomicInteger(0);
    }

    @Override
    public void run() {
        // Bucle principal que se ejecuta mientras haya entregas pendientes
        while (!gestor.isEntregaDone()) {
            try {
                // Bloque sincronizado para esperar si no hay pedidos en tránsito
                synchronized (gestor.getMonitorEntrega()) {
                    while (gestor.getPedEnTran().getContador() == 0 && !gestor.isDespachoDone()) {
                        gestor.getMonitorEntrega().wait();
                    }
                }

                // Verifica si se han completado todas las entregas
                if (gestor.getPedEnTran().getContador() <= 0 && gestor.isDespachoDone()) {
                    gestor.markEntregaDone();
                    System.out.println("FIN DE ENTREGA");
                    synchronized (gestor.getMonitorVerificacion()) {
                        gestor.getMonitorVerificacion().notify();
                    }
                    break;
                }

                // Muestra cuántos pedidos faltan por despachar
                System.out.println("Quedan despachar " + (500 - gestor.getDespachados()));
                
                // Simula una entrega con 90% de probabilidad de éxito
                boolean entregaExitosa = random.nextInt(100) < 90;

                // Bloque sincronizado para procesar un pedido en tránsito
                synchronized (gestor.getPedEnTran()) {
                    if (gestor.getPedEnTran().getContador() > 0) {
                        // Elimina el pedido de la lista de tránsito
                        gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");
                        
                        if (entregaExitosa) {
                            // Si la entrega fue exitosa, registra el pedido como entregado
                            gestor.modificarRegistro(gestor.getPedEntregado(), "AGREGAR");
                            int resultado = gestor.procesarPedido("ENTREGADO");
                            if (resultado != -1) {
                                synchronized (gestor.getMonitorVerificacion()) {
                                    gestor.getMonitorVerificacion().notify();
                                }
                            }
                        } else {
                            // Si la entrega falló, registra el pedido como fallido
                            gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                            contador.incrementAndGet();
                        }
                    }
                }
                // Simula el tiempo de entrega
                DormirHilo();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Método auxiliar para simular el tiempo de entrega
    private void DormirHilo() {
        try {
            // Genera una demora aleatoria entre tiempoMin y tiempoMax
            int demora = ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1);
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


