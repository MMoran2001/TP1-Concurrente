import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainSecuencial {
    private static final int TOTAL_PEDIDOS = 500;
    private static final int TIEMPO_MIN = 30;
    private static final int TIEMPO_MAX = 90;
    
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("=== Iniciando ejecución secuencial ===");
        System.out.println("Hora de inicio: " + sdf.format(new Date()));
        
        long inicio = System.currentTimeMillis();
        Gestor gestor = Gestor.getMiGestor();
        Random random = new Random();
        
        // Iniciar el hilo escritor para el registro
        HiloEscritor hiloLogger = new HiloEscritor();
        hiloLogger.start();
        
        // Variables para medir tiempos por etapa
        long tiempoPreparacion = 0;
        long tiempoDespacho = 0;
        long tiempoEntrega = 0;
        long tiempoVerificacion = 0;
        
        // Procesamiento secuencial de pedidos
        for (int i = 0; i < TOTAL_PEDIDOS; i++) {
            // Preparación
            long inicioPrep = System.currentTimeMillis();
            boolean pedidoPreparado = false;
            while (!pedidoPreparado) {
                int[] pos = gestor.randomPos();
                pedidoPreparado = gestor.TomarPedido(pos[0], pos[1]);
                if (pedidoPreparado) {
                    dormir(TIEMPO_MIN, TIEMPO_MAX);
                }
            }
            gestor.procesarPedido("PREPARADO");
            tiempoPreparacion += System.currentTimeMillis() - inicioPrep;
            
            // Despacho
            long inicioDesp = System.currentTimeMillis();
            int[] pos = buscarCasilleroOcupado(gestor);
            boolean exitosoDespacho = random.nextInt(100) <= 85;
            
            synchronized(gestor.getAlmacen()[pos[0]][pos[1]]) {
                if (exitosoDespacho) {
                    gestor.getAlmacen()[pos[0]][pos[1]].cambiarEstado(Estado_Casilleros.VACIO);
                    gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                    gestor.modificarRegistro(gestor.getPedEnTran(), "AGREGAR");
                } else {
                    gestor.getAlmacen()[pos[0]][pos[1]].cambiarEstado(Estado_Casilleros.FUERA_DE_SERVICIO);
                    gestor.modificarRegistro(gestor.getPedEnPrep(), "ELIMINAR");
                    gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                }
            }
            gestor.procesarPedido("DESPACHADO");
            dormir(TIEMPO_MIN, TIEMPO_MAX);
            tiempoDespacho += System.currentTimeMillis() - inicioDesp;
            
            // Entrega
            long inicioEntr = System.currentTimeMillis();
            if (exitosoDespacho) {
                boolean exitosoEntrega = random.nextInt(100) < 90;
                gestor.modificarRegistro(gestor.getPedEnTran(), "ELIMINAR");
                
                if (exitosoEntrega) {
                    gestor.modificarRegistro(gestor.getPedEntregado(), "AGREGAR");
                    gestor.procesarPedido("ENTREGADO");
                    
                    // Verificación
                    long inicioVerif = System.currentTimeMillis();
                    boolean exitosoVerificacion = random.nextInt(100) < 95;
                    gestor.modificarRegistro(gestor.getPedEntregado(), "ELIMINAR");
                    if (exitosoVerificacion) {
                        gestor.modificarRegistro(gestor.getPedVerificado(), "AGREGAR");
                    } else {
                        gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                    }
                    dormir(TIEMPO_MIN, TIEMPO_MAX);
                    tiempoVerificacion += System.currentTimeMillis() - inicioVerif;
                } else {
                    gestor.modificarRegistro(gestor.getPedFallido(), "AGREGAR");
                }
                dormir(TIEMPO_MIN, TIEMPO_MAX);
            }
            tiempoEntrega += System.currentTimeMillis() - inicioEntr;
            
            // Mostrar progreso
            if (i % 50 == 0) {
                System.out.println("\nProgreso - Pedido " + i);
                System.out.println("Verificados: " + gestor.getPedVerificado().getContador());
                System.out.println("Fallidos: " + gestor.getPedFallido().getContador());
                System.out.println("Tiempo transcurrido: " + (System.currentTimeMillis() - inicio) / 1000.0 + " segundos");
            }
        }
        
        // Marcar como terminado para el registro
        gestor.markPreparacionDone();
        gestor.markDespachoDone();
        gestor.markEntregaDone();
        gestor.markVerificacionDone();
        
        long fin = System.currentTimeMillis();
        long duracionTotal = fin - inicio;
        
        System.out.println("\n=== Resultados de la ejecución secuencial ===");
        System.out.println("Hora de finalización: " + sdf.format(new Date()));
        System.out.println("\nTiempos por etapa:");
        System.out.println("- Preparación: " + (tiempoPreparacion / 1000.0) + " segundos");
        System.out.println("- Despacho: " + (tiempoDespacho / 1000.0) + " segundos");
        System.out.println("- Entrega: " + (tiempoEntrega / 1000.0) + " segundos");
        System.out.println("- Verificación: " + (tiempoVerificacion / 1000.0) + " segundos");
        System.out.println("\nTiempo total de ejecución: " + (duracionTotal / 1000.0) + " segundos");
        System.out.println("\nEstadísticas de pedidos:");
        System.out.println("- Pedidos verificados: " + gestor.getPedVerificado().getContador());
        System.out.println("- Pedidos fallidos: " + gestor.getPedFallido().getContador());
        System.out.println("- Total de pedidos procesados: " + 
            (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador()));
        System.out.println("\nEstadísticas de casilleros:");
        System.out.println(gestor.getCasilleroMasUsado());
        
        // Registrar tiempo total en el archivo de registro
        hiloLogger.registrarTiempoEjecucion(duracionTotal);
        
        // Detener el hilo escritor
        hiloLogger.detener();
    }
    
    private static void dormir(int tiempoMin, int tiempoMax) {
        try {
            int demora = new Random().nextInt(tiempoMax - tiempoMin + 1) + tiempoMin;
            Thread.sleep(demora);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static int[] buscarCasilleroOcupado(Gestor gestor) {
        Casillero[][] almacen = gestor.getAlmacen();
        int[] pos = gestor.randomPos();
        
        while(almacen[pos[0]][pos[1]].getEstado() != Estado_Casilleros.OCUPADO) {
            synchronized(almacen[pos[0]][pos[1]]) {
                pos = gestor.randomPos();
            }
        }
        
        return pos;
    }
} 