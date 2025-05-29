import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiloEscritor extends Thread {
    private final File archivoLog;
    private volatile boolean activo = true; // para detener el hilo más adelante si querés
    private final Gestor gestor;
    private long tiempoFinal = -1;

    public HiloEscritor() {
        gestor = Gestor.getMiGestor();
        archivoLog = new File("Registro de ejecucion.txt");
        // Limpiar el archivo al inicio
        try {
            new FileWriter(archivoLog, false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (FileWriter writer = new FileWriter(archivoLog)) {
            while (activo) {
                String Resultados = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                writer.write(Resultados + "\n");
                writer.write("Cantidad de pedidos verificados: " + gestor.getPedVerificado().getContador() + "\n"
                        + "Cantidad de pedidos fallidos: " + gestor.getPedFallido().getContador() + "\n"
                        + "Total de pedidos procesados: " + (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador()) + "\n\n");
                writer.flush();
                System.out.println("Archivo creado en: " + archivoLog.getAbsolutePath());
                Thread.sleep(200);
            }
            if (tiempoFinal != -1) {
                writer.write("\nFIN DE EJECUCIÓN \n");
                writer.write("Tiempo total de ejecución: " + tiempoFinal + " ms\n");
                writer.write("Total final de pedidos verificados: " + gestor.getPedVerificado().getContador() + "\n");
                writer.write("Total final de pedidos fallidos: " + gestor.getPedFallido().getContador() + "\n");
                writer.write("Total final de pedidos procesados: " + (gestor.getPedVerificado().getContador() + gestor.getPedFallido().getContador()) + "\n");
                writer.flush();
            }
            registrarCasilleroMasUsado(gestor.getCasilleroMasUsado());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error en el LoggerHilo: " + e.getMessage());
        }
    }

    public void detener() {
        activo = false;// por si está dormido
    }

    public void registrarTiempoEjecucion(long tiempo) {
        this.tiempoFinal = tiempo;
    }

    public void registrarCasilleroMasUsado(String info) {
        try (FileWriter writer = new FileWriter(archivoLog, true)) {
            writer.write(info + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

